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
import net.questionbank.dto.textbook.TextbookApiResponse;
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
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

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

        TextbookApiResponse responseDTO = webClient
                .post()
                .uri("/chapter/subjectInfo-list")
                .bodyValue(TextBookRequestDTO.builder().subjectId((long) subjectId).build())
                .retrieve()
                .bodyToMono(TextbookApiResponse.class).block();

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
                .filePath(testDTO.getPdfFileId())
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

    @Override
    public Map<String, List<String>> testPdfImageList(List<QuestionApiDTO> questionsFromApi) {

        if (questionsFromApi == null || questionsFromApi.isEmpty()) return null;

        Map<String, List<String>> map = new HashMap<>();

        List<String> all = new ArrayList<>();

        int startNo = 0;
        int passageIndex = 0;
        Long cpid = 0L;

        try {
            QuestionApiDTO question = null;

            for (QuestionApiDTO questionApiDTO : questionsFromApi) {
                question = questionApiDTO;

                if (!cpid.equals(question.getPassageId())) {
                    if (startNo > 0 && startNo < question.getItemNo() - 1) {
                        all.set(passageIndex, "[" + startNo + "-" + (question.getItemNo() - 1) + "]");
                    }
                    passageIndex = all.size();
                    all.add("");
                    all.add(question.getPassageUrl());
                    startNo = question.getItemNo();
                }

                cpid = question.getPassageId() != null ? question.getPassageId() : 0L;

                all.add(question.getItemNo() + ".");
                all.add(question.getQuestionUrl());
                all.add("(답)");
                all.add(question.getAnswerUrl());
                all.add("(해설)");
                all.add(question.getExplainUrl());

            }

            cpid = 0L;
            if (!cpid.equals(question.getPassageId())) {
                if (startNo > 0 && startNo < question.getItemNo()) {
                    all.set(passageIndex, "[" + startNo + "-" + (question.getItemNo()) + "]");
                }
            }

            List<String> questions = all.stream().filter(str -> {
                if(str.startsWith("(")) {
                    return false;
                }
                if(str.contains("answer")) {
                    return false;
                }
                if(str.contains("explain")) {
                    return false;
                }
                return true;
            }).toList();

            List<String> answers = all.stream().filter(str -> {
                if(str.startsWith("(")) {
                    return true;
                }
                if(str.contains("answer")) {
                    return true;
                }
                if(str.contains("explain")) {
                    return true;
                }
                if(str.endsWith(".")) {
                    return true;
                }
                if(str.contains("question")) {
                    return true;
                }
                return false;
            }).map(str -> {
                if(str.contains("question")) return "";
                return str;
            }).toList();


            map.put("all", all);
            map.put("questions", questions);
            map.put("answers", answers);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return map;
    }


    //svg -> png 파일 저장까지
    public String convertSvgToPng(String url, Long itemId, int type) throws IOException, TranscoderException {

        String pngPath = itemId + "_" + type + ".png";

//        return url;

        if (new File(fileDir + pngPath).exists()) {
            return fileUri + pngPath;
        }

        InputStream svgInputStream = new URL(url).openStream();
        OutputStream pngOutputStream = new FileOutputStream(fileDir + pngPath);

        PNGTranscoder transcoder = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(svgInputStream);
        TranscoderOutput output = new TranscoderOutput(pngOutputStream);
        transcoder.transcode(input, output);

        svgInputStream.close();
        pngOutputStream.close();

//        return "file:///E:/QuestionBank/src/main/resources/static/convertPng/" + pngPath;
        return fileUri + pngPath;
    }
}
