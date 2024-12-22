package net.questionbank.service.step3;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.domain.Member;
import net.questionbank.domain.Question;
import net.questionbank.domain.Test;
import net.questionbank.domain.Textbook;
import net.questionbank.dto.question.QuestionApiDTO;
import net.questionbank.dto.question.QuestionRequestDTO;
import net.questionbank.dto.question.QuestionResponseDTO;
import net.questionbank.dto.test.TempTestDTO;
import net.questionbank.dto.test.TestDTO;
import net.questionbank.dto.test.TestDataDTO;
import net.questionbank.dto.test.TestDataResponseDTO;
import net.questionbank.dto.textbook.TextBookApiDTO;
import net.questionbank.dto.textbook.TextBookRequestDTO;
import net.questionbank.dto.textbook.TextBookResponseDTO;
import net.questionbank.mapper.QuestionMapper;
import net.questionbank.mapper.TestMapper;
import net.questionbank.repository.QuestionRepository;
import net.questionbank.repository.TestRepository;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class Step3ServiceImpl implements Step3Service {
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final WebClient webClient;
    private final QuestionMapper questionMapper;
    private final TestMapper testMapper;

    @Value("${file.imageDir}")
    private String fileDir;
    @Value("${file.imageUri}")
    private String fileUri;

    @Override
    public TempTestDTO testInfo(List<Long> itemIdList, Long subjectId) {
        List<QuestionApiDTO> questionsFromApi = getQuestionsFromApi(itemIdList);
        return TempTestDTO.builder()
                .questions(questionsFromApi)
                .textbookApiDTO(getTextBookFromApi(subjectId))
                .imageList(testPdfImageList(questionsFromApi))
                .build();
    }

    @Override
    public List<QuestionApiDTO> getQuestionsFromApi(List<Long> itemIdList) {
        QuestionRequestDTO questionRequestDTO = QuestionRequestDTO.builder().itemIdList(itemIdList).build();
        return Objects.requireNonNull(webClient
                .post()
                .uri("/item-img/item-list")
                .bodyValue(questionRequestDTO)
                .retrieve()
                .bodyToMono(QuestionResponseDTO.class).block()).getItemList();
    }

    @Override
    public TextBookApiDTO getTextBookFromApi(Long subjectId) {

//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("subjectId", textBookId);

        TextBookResponseDTO responseDTO = webClient
                .post()
                .uri("/chapter/subjectInfo-list")
                .bodyValue(TextBookRequestDTO.builder().subjectId((long) subjectId).build())
                .retrieve()
                .bodyToMono(TextBookResponseDTO.class).block();

        if (responseDTO == null || !responseDTO.getSuccessYn().equals("Y")) return null;

        if (responseDTO.getSubjectInfoList().isEmpty()) return null;

        return responseDTO.getSubjectInfoList().get(0);
    }

    @Override
    @Transactional
    public void saveTestInfo(TestDTO testDTO, List<Long> questionIdList) throws IllegalStateException, IllegalArgumentException {
        if (questionIdList == null || questionIdList.isEmpty()) throw new IllegalArgumentException("시험지 문항을 선택해주세요.");

        Test test = Test.builder()
                .title(testDTO.getTitle())
                .createdAt(LocalDateTime.now())
                .member(Member.builder().memberId(testDTO.getUserId()).build())
                .textbook(Textbook.builder().textbookId(testDTO.getSubjectId().intValue()).build())
                .build();
        testRepository.save(test);

        if (test.getTestId() == 0) {
            throw new IllegalStateException("저장 실패");
        }

        List<Question> questions = questionRepository.saveAll(questionIdList.stream().map(id -> Question.builder()
                        .itemId(id.intValue())
                        .test(test)
                        .itemNo(questionIdList.indexOf(id) + 1)
                        .build()
                ).toList()
        );

        if (!questions.stream().filter(question -> question.getItemId() == 0).toList().isEmpty()) {
            throw new IllegalStateException("저장 실패");
        }

        // 전송 실패했을 때 어떻게 할지 정해야함
        // 일단 로그만 찍음
//        try {
//            boolean b = sendTestInfo(TestDataDTO.builder()
//                    .examId((long) test.getTestId())
//                    .examName(test.getTitle())
//                    .itemList(questionIdList)
//                    .teacherId(test.getMember().getMemberId())
//                    .teacherName(test.getMember().getName())
//                    .subjectName(testDTO.getSubjectName())
//                    .build()
//            );
//        } catch (IllegalStateException e) {
//            log.error(e.getMessage());
//        } catch (Exception e) {
//            log.error("오류");
//        }

    }

    @Override
    public boolean sendTestInfo(TestDataDTO testDataDto) throws IllegalStateException {
        TestDataResponseDTO block = webClient.post().uri("http주소/api/exam")
                .bodyValue(testDataDto)
                .retrieve()
                .bodyToMono(TestDataResponseDTO.class).block();

        if (block == null) throw new IllegalStateException("시험지 정보 전송 실패");

        return block.getSuccess();
    }

    //    @Override
    public List<String> testPdfImageList(List<QuestionApiDTO> questionsFromApi) {
//        List<QuestionApiDTO> questionsFromApi = getQuestionsFromApi(itemIdList);

        if (questionsFromApi == null || questionsFromApi.isEmpty()) return null;

        List<String> resultList = new ArrayList<>();

        int startNo = 0;
        int passageIndex = 0;
        Long cpid = 0L;

        try {
            QuestionApiDTO question = null;

            for (QuestionApiDTO questionApiDTO : questionsFromApi) {
                question = questionApiDTO;

                if (!cpid.equals(question.getPassageId())) {
                    if (startNo < question.getItemNo() - 1) {
                        resultList.set(passageIndex, "[" + startNo + "-" + (question.getItemNo() - 1) + "]");
                    }
                    passageIndex = resultList.size();
                    resultList.add("");
                    resultList.add(convertSvgToPng(question.getPassageUrl(), question.getItemId(), 1));
                    startNo = question.getItemNo();
                }

                cpid = question.getPassageId() != null ? question.getPassageId() : 0L;

                resultList.add(question.getItemNo() + ".");
                resultList.add(convertSvgToPng(question.getQuestionUrl(), question.getItemId(), 2));
                resultList.add("[답]");
                resultList.add(convertSvgToPng(question.getAnswerUrl(), question.getItemId(), 3));
                resultList.add("[해설]");
                resultList.add(convertSvgToPng(question.getExplainUrl(), question.getItemId(), 4));
            }

            if (!cpid.equals(question.getPassageId())) {
                if (startNo < question.getItemNo() - 1) {
                    resultList.set(passageIndex, "[" + startNo + "-" + (question.getItemNo() - 1) + "]");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return resultList;
    }


    public String convertSvgToPng(String url, Long itemId, int type) throws IOException, TranscoderException {

        String pngPath = itemId + "_" + type + ".png";

        return url;

//        if (new File(fileDir + pngPath).exists()) {
//            return fileUri + pngPath;
//        }
//
//        InputStream svgInputStream = new URL(url).openStream();
//        OutputStream pngOutputStream = new FileOutputStream(fileDir + pngPath);
//
//        PNGTranscoder transcoder = new PNGTranscoder();
//        TranscoderInput input = new TranscoderInput(svgInputStream);
//        TranscoderOutput output = new TranscoderOutput(pngOutputStream);
//        transcoder.transcode(input, output);
//
//        svgInputStream.close();
//        pngOutputStream.close();
//
////        return "file:///E:/QuestionBank/src/main/resources/static/convertPng/" + pngPath;
//        return fileUri + pngPath;
    }
}
