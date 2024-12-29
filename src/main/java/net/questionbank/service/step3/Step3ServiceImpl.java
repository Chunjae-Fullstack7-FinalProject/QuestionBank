package net.questionbank.service.step3;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.domain.Member;
import net.questionbank.domain.Question;
import net.questionbank.domain.Test;
import net.questionbank.domain.Textbook;
import net.questionbank.dto.question.*;
import net.questionbank.dto.test.*;
import net.questionbank.dto.textbook.TextBookApiDTO;
import net.questionbank.dto.textbook.TextBookRequestDTO;
import net.questionbank.dto.textbook.TextbookApiResponse;
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

    @Value("${file.imageDir}")
    private String fileDir;
    @Value("${file.imageUri}")
    private String fileUri;

    @Override
    public TempTestImageDTO testInfoImage(List<Long> itemIdList, Long subjectId) {
        List<QuestionImageApiDTO> questionsFromApi = getQuestionsImageFromApi(itemIdList);
        return TempTestImageDTO.builder()
                .questions(questionsFromApi)
                .textbookApiDTO(getTextBookFromApi(subjectId))
                .imageList(testPdfImageList(questionsFromApi))
                .build();
    }

    @Override
    public TempTestHtmlDTO testInfoHtml(List<Long> itemIdList, Long subjectId) {
        List<QuestionHtmlApiDTO> questionsFromApi = getQuestionsHtmlFromApi(itemIdList);
        return TempTestHtmlDTO.builder()
                .questions(questionsFromApi)
                .textbookApiDTO(getTextBookFromApi(subjectId))
                .htmlList(testPdfHtmlList(questionsFromApi))
                .build();
    }

    @Override
    public List<QuestionImageApiDTO> getQuestionsImageFromApi(List<Long> itemIdList) {
        QuestionRequestDTO questionRequestDTO = QuestionRequestDTO.builder().itemIdList(itemIdList).build();
        return Objects.requireNonNull(webClient
                .post()
                .uri("/item-img/item-list")
                .bodyValue(questionRequestDTO)
                .retrieve()
                .bodyToMono(QuestionImageResponseDTO.class).block()).getItemList();
    }

    @Override
    public List<QuestionHtmlApiDTO> getQuestionsHtmlFromApi(List<Long> itemIdList) {
        QuestionRequestDTO questionRequestDTO = QuestionRequestDTO.builder().itemIdList(itemIdList).build();
        return Objects.requireNonNull(webClient
                .post()
                .uri("/item/item-list")
                .bodyValue(questionRequestDTO)
                .retrieve()
                .bodyToMono(QuestionHtmlResponseDTO.class).block()).getItemList();
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
    public void saveTestInfo(TestSaveDTO testSaveDTO, List<Long> questionIdList) throws IllegalStateException, IllegalArgumentException {
        if (questionIdList == null || questionIdList.isEmpty()) throw new IllegalArgumentException("시험지 문항을 선택해주세요.");

        Test test = Test.builder()
                .title(testSaveDTO.getTitle())
                .createdAt(LocalDateTime.now())
                .member(Member.builder().memberId(testSaveDTO.getUserId()).name(testSaveDTO.getUserName()).build())
                .textbook(Textbook.builder().textbookId(testSaveDTO.getSubjectId().intValue()).build())
                .filePath(testSaveDTO.getPdfFileId())
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
        // 서술형 있으면 제외
//        if(testDTO.isDescriptive()) {
//            return;
//        }
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
        TestDataResponseDTO block = webClient.post().uri("https://www.gyeongminiya.asia/api/exam")
                .bodyValue(testDataDto)
                .retrieve()
                .bodyToMono(TestDataResponseDTO.class).block();

        if (block == null) throw new IllegalStateException("시험지 정보 전송 실패");

        return block.getSuccess();
    }

    @Override
    public Map<String, List<String>> testPdfImageList(List<QuestionImageApiDTO> questionsFromApi) {

        if (questionsFromApi == null || questionsFromApi.isEmpty()) return null;

        Map<String, List<String>> map = new HashMap<>();

        List<String> all = new ArrayList<>();

        int startNo = 0;
        int passageIndex = 0;
        Long cpid = 0L;

        try {
            QuestionImageApiDTO question = null;

            for (QuestionImageApiDTO questionApiDTO : questionsFromApi) {
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
                if (str.startsWith("(")) {
                    return false;
                }
                if (str.contains("answer")) {
                    return false;
                }
                if (str.contains("explain")) {
                    return false;
                }
                return true;
            }).toList();

            List<String> answers = all.stream().filter(str -> {
                if (str.startsWith("(")) {
                    return true;
                }
                if (str.contains("answer")) {
                    return true;
                }
                if (str.contains("explain")) {
                    return true;
                }
                if (str.endsWith(".")) {
                    return true;
                }
                if (str.contains("question")) {
                    return true;
                }
                return false;
            }).map(str -> {
                if (str.contains("question")) return "";
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

    @Override
    public Map<String, List<String>> testPdfHtmlList(List<QuestionHtmlApiDTO> questionsFromApi) {

        if (questionsFromApi == null || questionsFromApi.isEmpty()) return null;

        Map<String, List<String>> map = new HashMap<>();

        List<String> all = new ArrayList<>();

        int startNo = 0;
        int passageIndex = 0;
        Long cpid = 0L;

        try {
            QuestionHtmlApiDTO question = null;

            for (QuestionHtmlApiDTO questionApiDTO : questionsFromApi) {
                question = questionApiDTO;

                if (!cpid.equals(question.getPassageId()) && question.getPassageId() != null) {
                    if (startNo > 0 && startNo < question.getItemNo() - 1) {
                        all.set(passageIndex, all.get(passageIndex)
                                        .replaceFirst("<span class=\"txt \">", "<div class=\"paragraph pdf-passage-no\" style=\"border-left:0.2mm none;border-right:0.2mm none;border-top:0.2mm none;border-bottom:0.2mm none;text-indent: 14px;margin-left: 0px;margin-right: 0px;\">\n         <span class=\"txt \"><strong>[" + startNo + "-" + (question.getItemNo() - 1) + "]</strong></span>\n        </div>\n<span class=\"txt \">")
//                                .replaceFirst("<caption></caption>","<caption>[" + startNo + "-" + (question.getItemNo() - 1) + "]</caption>")
                        );
                    }
                    passageIndex = all.size();
                    all.add(question.getPassageHtml()
                            .replace("<html>\n <head></head>\n <body>\n ", "<div class=\"pdf-item pdf-item-question\">")
                            .replace(" </body>\n</html>", "</div>")
                            .replaceFirst("class=\"paragraph\"", "class=\"paragraph pdf-item-passage\"")
                            .replace("class=\"paragraph\"", "class=\"paragraph pdf-line\"")
                    );
                    startNo = question.getItemNo();
                }

                cpid = question.getPassageId() != null ? question.getPassageId() : 0L;

                String html = question.getQuestionHtml()
                        .replace("<html>\n <head></head>\n <body>\n ", "<div class=\"pdf-item pdf-item-question\">")
                        .replace(" </body>\n</html>", "</div>")
                        .replaceFirst("class=\"paragraph\"", "class=\"paragraph pdf-item-question-main\"")
                        .replaceFirst("<span class=\"txt \">", "<span class=\"txt\"><strong>" + question.getItemNo() + ".</strong>&nbsp;</span>\n<span class=\"txt \">");

                if (html.contains("<table")) {
                    html = html.replace("<html>\n <head></head>\n <body>\n ", "<div class=\"pdf-item pdf-item-question\">")
                            .replace(" </body>\n</html>", "</div>")
                            .replace("class=\"paragraph\"", "class=\"paragraph pdf-line\"")
                    ;
                }

                all.add(html);

                if (question.getChoice1Html() != null && !question.getChoice1Html().isEmpty()) {
                    all.add(question.getChoice1Html().replace("<html>\n <head></head>\n <body>\n ", "<div class=\"pdf-item pdf-item-choice\">")
                            .replace(" </body>\n</html>", "</div>")
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">①&nbsp;")
                    );
                }
                if (question.getChoice2Html() != null && !question.getChoice2Html().isEmpty()) {
                    all.add(question.getChoice2Html().replace("<html>\n <head></head>\n <body>\n ", "<div class=\"pdf-item pdf-item-choice\">")
                            .replace(" </body>\n</html>", "</div>")
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">②&nbsp;")
                    );
                }
                if (question.getChoice3Html() != null && !question.getChoice3Html().isEmpty()) {
                    all.add(question.getChoice3Html().replace("<html>\n <head></head>\n <body>\n ", "<div class=\"pdf-item pdf-item-choice\">")
                            .replace(" </body>\n</html>", "</div>")
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">③&nbsp;")
                    );
                }
                if (question.getChoice4Html() != null && !question.getChoice4Html().isEmpty()) {
                    all.add(question.getChoice4Html().replace("<html>\n <head></head>\n <body>\n ", "<div class=\"pdf-item pdf-item-choice\">")
                            .replace(" </body>\n</html>", "</div>")
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">④&nbsp;")
                    );
                }
                if (question.getChoice5Html() != null && !question.getChoice5Html().isEmpty()) {
                    all.add(question.getChoice5Html().replace("<html>\n <head></head>\n <body>\n ", "<div class=\"pdf-item pdf-item-choice\">")
                            .replace(" </body>\n</html>", "</div>")
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">⑤&nbsp;")
                    );
                }

                all.add(question.getAnswerHtml().replace("<html>\n <head></head>\n <body>\n ", "<div class=\"pdf-item pdf-item-answer\">")
                        .replace(" </body>\n</html>", "</div>")
                        .replaceFirst("<span class=\"txt \">", "<span class=\"txt pdf-answer\">(답)<br></span>\n<span class=\"txt \">")
                );
                all.add(question.getExplainHtml().replace("<html>\n <head></head>\n <body>\n ", "<div class=\"pdf-item pdf-item-answer\">")
                                .replace(" </body>\n</html>", "</div>")
                                .replaceFirst("<span class=\"txt \">", "<span class=\"txt pdf-answer\">(해설)<br></span>\n<span class=\"txt \">")
//                        .replace("class=\"paragraph\"", "class=\"paragraph pdf-item pdf-item-answer\"")
                );

            }

            cpid = 0L;
            if (!cpid.equals(question.getPassageId())) {
                if (startNo > 0 && startNo < question.getItemNo()) {
                    all.set(passageIndex, all.get(passageIndex)
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">[" + startNo + "-" + (question.getItemNo()) + "]</span>\n<span class=\"txt \">"));
                    all.set(passageIndex, "[" + startNo + "-" + (question.getItemNo()) + "]");
                }
            }

            all = all.stream().map(str ->
                                    str.replace("style=\"border-left:0.2mm none;border-right:0.2mm none;border-top:0.2mm none;border-bottom:0.2mm none;text-indent: 14px;margin-left: 0px;margin-right: 0px;\"", "")
                                            .replace("style=\"border-left:0.2mm none;border-right:0.2mm none;border-top:0.2mm none;border-bottom:0.2mm none;text-indent: 0px;margin-left: 0px;margin-right: 0px;\"", "")
//                                    .replace("class=\"paragraph\"", "class=\"paragraph inner-item\"")
                    )
                    .toList();

            //문제만 추출
            List<String> questions = all.stream().filter(str -> !str.contains("pdf-item-answer")).toList();
            List<String> answers = all.stream().filter(str -> str.contains("pdf-item-answer")).toList();


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
