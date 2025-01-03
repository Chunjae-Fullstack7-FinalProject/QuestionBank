package net.questionbank.service.step3;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @Value("${file.pdfDir}")
    private String pdfDir;

    @Override
    public TempTestImageDTO testInfoImage(List<Long> itemIdList, Long subjectId) {
        List<QuestionImageApiDTO> questionsFromApi = getQuestionsImageFromApi(itemIdList);
        return TempTestImageDTO.builder()
                .questions(questionsFromApi)
                .textbookApiDTO(getTextBookFromApi(subjectId))
//                .imageList(testPdfImageList(questionsFromApi))
                .imageHtml(testPdfImageStringList(questionsFromApi))
                .build();
    }

    @Override
    public TempTestHtmlDTO testInfoHtml(List<Long> itemIdList, Long subjectId) {
        List<QuestionHtmlApiDTO> questionsFromApi = getQuestionsHtmlFromApi(itemIdList);
        return TempTestHtmlDTO.builder()
                .questions(questionsFromApi)
//                .textbookApiDTO(getTextBookFromApi(subjectId))
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
        if (testSaveDTO.isDescriptive()) {
            return;
        }
        try {
            boolean b = sendTestInfo(TestDataDTO.builder()
                    .examId((long) test.getTestId())
                    .examName(test.getTitle())
                    .itemList(questionIdList)
                    .teacherId(test.getMember().getMemberId())
                    .teacherName(test.getMember().getName())
                    .subjectName(testSaveDTO.getSubjectName())
                    .build()
            );
            if (!b) {
                log.error("전송 실패");
            }
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("오류");
        }

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
    public List<String> testPdfImageStringList(List<QuestionImageApiDTO> questionsFromApi) {

        if (questionsFromApi == null || questionsFromApi.isEmpty()) return null;

        List<String> all = new ArrayList<>();

        int startNo = 0;
        int passageIndex = 0;
        Long prevId = 0L;


        String passage = """
                <div class="pdf-item pdf-passage">
                   <div class="pdf-txt pdf-txt-no">TEXT</div>
                   <img class="pdf-img" src="URL" />
                </div>
                """;
        String question = """
                <div class="pdf-item pdf-question pdf-question-grid">
                   <div class="pdf-txt pdf-txt-no">TEXT</div>
                   <div><img class="pdf-img" src="URL" /></div>
                </div>
                """;
        String answer = """
                <div class="pdf-item pdf-answer">
                   <div class="pdf-txt pdf-txt-no pdf-display-none">NO</div>
                   <div class="pdf-txt pdf-txt-title">TEXT</div>
                   <img class="pdf-img" src="URL" />
                </div>
                """;
        String explain = """
                <div class="pdf-item pdf-explain">
                   <div class="pdf-txt pdf-txt-title">TEXT</div>
                   <img class="pdf-img" src="URL" />
                </div>
                """;
        try {
            QuestionImageApiDTO questionInfo = null;

            for (QuestionImageApiDTO questionApiDTO : questionsFromApi) {
                questionInfo = questionApiDTO;

                if (questionInfo.getPassageId() != null && !questionInfo.getPassageId().equals(0L)) {
                    if (prevId == null || prevId == 0) {
                        passageIndex = all.size();
                        all.add(passage.replace("URL", questionInfo.getPassageUrl()));
                        startNo = questionInfo.getItemNo();
                    } else if (!prevId.equals(questionInfo.getPassageId())) {


                        if (startNo > 0) {
                            if (startNo == questionInfo.getItemNo() - 1) {
                                all.set(passageIndex, all.get(passageIndex).replace("TEXT", ""));
                            } else {
                                all.set(passageIndex, all.get(passageIndex).replace("TEXT", "[" + startNo + "-" + (questionInfo.getItemNo() - 1) + "]"));
                            }
                        }
                        startNo = questionInfo.getItemNo();
                        passageIndex = all.size();
                        all.add(passage.replace("URL", questionInfo.getPassageUrl()));
                    }
                } else {
                    startNo = questionInfo.getItemNo();
                }

                prevId = questionInfo.getPassageId();

                all.add(question.replace("TEXT", String.valueOf(questionInfo.getItemNo()+".&nbsp;")).replace("URL", questionInfo.getQuestionUrl()));
                all.add(answer.replace("NO", String.valueOf(questionInfo.getItemNo()+".&nbsp;")).replace("TEXT", "(답)").replace("URL", questionInfo.getAnswerUrl()));
                all.add(explain.replace("TEXT", "(해설)").replace("URL", questionInfo.getExplainUrl()));
            }

            if (startNo != questionsFromApi.size()) {
                all.set(passageIndex, all.get(passageIndex).replace("TEXT", "[" + startNo + "-" + questionsFromApi.size() + "]"));
            }
            else {
                all.set(passageIndex, all.get(passageIndex).replace("TEXT", ""));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return all;
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
                                        .replaceFirst("<span class=\"txt \">", "<div class=\"paragraph pdf-passage-no\">         <span class=\"txt \"><strong>[" + startNo + "-" + (question.getItemNo() - 1) + "]</strong></span>        </div><span class=\"txt \">")
//                                .replaceFirst("<caption></caption>","<caption>[" + startNo + "-" + (question.getItemNo() - 1) + "]</caption>")
                        );
                    }
                    passageIndex = all.size();
                    all.add(question.getPassageHtml()
                            .replace("<html> <head></head> <body> ", "<div class=\"pdf-item pdf-item-question\">")
                            .replace(" </body></html>", "</div>")
                            .replaceFirst("class=\"paragraph\"", "class=\"paragraph pdf-item-passage\"")
                            .replace("class=\"paragraph\"", "class=\"paragraph pdf-line\"")
                    );
                    startNo = question.getItemNo();
                }

                cpid = question.getPassageId() != null ? question.getPassageId() : 0L;

                String html = question.getQuestionHtml()
                        .replace("<html> <head></head> <body> ", "<div class=\"pdf-item pdf-item-question\">")
                        .replace(" </body></html>", "</div>")
                        .replaceFirst("class=\"paragraph\"", "class=\"paragraph pdf-item-question-main\"")
                        .replaceFirst("<span class=\"txt \">", "<span class=\"txt\"><strong>" + question.getItemNo() + ".</strong>&nbsp;</span><span class=\"txt \">");

                if (html.contains("<table")) {
                    html = html.replace("<html> <head></head> <body> ", "<div class=\"pdf-item pdf-item-question\">")
                            .replace(" </body></html>", "</div>")
                            .replace("class=\"paragraph\"", "class=\"paragraph pdf-line\"")
                    ;
                }

                all.add(html);

                StringBuilder sb = new StringBuilder();

                if (question.getChoice1Html() != null && !question.getChoice1Html().isEmpty()) {
                    sb.append("<div class=\"pdf-item pdf-item-choice\">");
                    sb.append(question.getChoice1Html().replace("<html> <head></head> <body> ", "")
                            .replace(" </body></html>", "")
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">①&nbsp;"));
//                    all.add(question.getChoice1Html().replace("<html> <head></head> <body> ", "<div class=\"pdf-item pdf-item-choice\">")
//                            .replace(" </body></html>", "</div>")
//                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">①&nbsp;")
//                    );
                }
                if (question.getChoice2Html() != null && !question.getChoice2Html().isEmpty()) {
                    sb.append(question.getChoice2Html().replace("<html> <head></head> <body> ", "")
                            .replace(" </body></html>", "")
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">②&nbsp;"));
//                    all.add(question.getChoice2Html().replace("<html> <head></head> <body> ", "<div class=\"pdf-item pdf-item-choice\">")
//                            .replace(" </body></html>", "</div>")
//                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">②&nbsp;")
//                    );
                }
                if (question.getChoice3Html() != null && !question.getChoice3Html().isEmpty()) {
                    sb.append(question.getChoice3Html().replace("<html> <head></head> <body> ", "")
                            .replace(" </body></html>", "")
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">③&nbsp;"));
//                    all.add(question.getChoice3Html().replace("<html> <head></head> <body> ", "<div class=\"pdf-item pdf-item-choice\">")
//                            .replace(" </body></html>", "</div>")
//                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">③&nbsp;")
//                    );
                }
                if (question.getChoice4Html() != null && !question.getChoice4Html().isEmpty()) {
                    sb.append(question.getChoice4Html().replace("<html> <head></head> <body> ", "")
                            .replace(" </body></html>", "")
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">④&nbsp;"));
//                    all.add(question.getChoice4Html().replace("<html> <head></head> <body> ", "<div class=\"pdf-item pdf-item-choice\">")
//                            .replace(" </body></html>", "</div>")
//                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">④&nbsp;")
//                    );
                }
                if (question.getChoice5Html() != null && !question.getChoice5Html().isEmpty()) {
                    sb.append(question.getChoice5Html().replace("<html> <head></head> <body> ", "")
                            .replace(" </body></html>", "")
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">⑤&nbsp;"));
//                    all.add(question.getChoice5Html().replace("<html> <head></head> <body> ", "<div class=\"pdf-item pdf-item-choice\">")
//                            .replace(" </body></html>", "</div>")
//                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">⑤&nbsp;")
//                    );
                }

                if (!sb.isEmpty()) {
                    sb.append("</div>");
                    all.add(sb.toString());
                }


                all.add(question.getAnswerHtml().replace("<html> <head></head> <body> ", "<div class=\"pdf-item pdf-item-answer\">")
                        .replace(" </body></html>", "</div>")
                        .replaceFirst("<span class=\"txt \">", "<span class=\"txt pdf-answer\">(답)<br></span><span class=\"txt \">")
                );
                all.add(question.getExplainHtml().replace("<html> <head></head> <body> ", "<div class=\"pdf-item pdf-item-answer\">")
                                .replace(" </body></html>", "</div>")
                                .replaceFirst("<span class=\"txt \">", "<span class=\"txt pdf-answer\">(해설)<br></span><span class=\"txt \">")
//                        .replace("class=\"paragraph\"", "class=\"paragraph pdf-item pdf-item-answer\"")
                );

            }

            cpid = 0L;
            if (!cpid.equals(question.getPassageId())) {
                if (startNo > 0 && startNo < question.getItemNo()) {
                    all.set(passageIndex, all.get(passageIndex)
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">[" + startNo + "-" + (question.getItemNo()) + "]</span><span class=\"txt \">"));
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
            //정답+해설만 추출
            List<String> answers = all.stream().filter(str -> str.contains("pdf-item-answer")).toList();


            map.put("all", all);
            map.put("questions", questions);
            map.put("answers", answers);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    @Override
    public Map<String, String> testPdfHtmlStringList(List<QuestionHtmlApiDTO> questionsFromApi) {

        if (questionsFromApi == null || questionsFromApi.isEmpty()) return null;

        Map<String, String> map = new HashMap<>();

        StringBuilder all = new StringBuilder();
        StringBuilder questions = new StringBuilder();
        StringBuilder answers = new StringBuilder();

        try {
            QuestionHtmlApiDTO question = null;

            for (QuestionHtmlApiDTO questionApiDTO : questionsFromApi) {
                question = questionApiDTO;

                if (question.getPassageId() != null) {
                    all.append(question.getPassageHtml());
                    questions.append(question.getPassageHtml());
                }

                all.append(question.getItemNo() + ". <br/>");
                questions.append(question.getItemNo() + ". <br/>");
                answers.append(question.getItemNo() + ". <br/>");

                all.append(question.getQuestionHtml());
                questions.append(question.getQuestionHtml());

                StringBuilder sb = new StringBuilder();

                if (question.getChoice1Html() != null && !question.getChoice1Html().isEmpty()) {
                    sb.append(question.getChoice1Html().replace("<html> <head></head> <body> ", "")
                            .replace(" </body></html>", "")
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">①&nbsp;"));
                }
                if (question.getChoice2Html() != null && !question.getChoice2Html().isEmpty()) {
                    sb.append(question.getChoice2Html().replace("<html> <head></head> <body> ", "")
                            .replace(" </body></html>", "")
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">②&nbsp;"));
                }
                if (question.getChoice3Html() != null && !question.getChoice3Html().isEmpty()) {
                    sb.append(question.getChoice3Html()
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">③&nbsp;"));
                }
                if (question.getChoice4Html() != null && !question.getChoice4Html().isEmpty()) {
                    sb.append(question.getChoice4Html()
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">④&nbsp;"));
                }
                if (question.getChoice5Html() != null && !question.getChoice5Html().isEmpty()) {
                    sb.append(question.getChoice5Html()
                            .replaceFirst("<span class=\"txt \">", "<span class=\"txt \">⑤&nbsp;"));

                }

                if (!sb.isEmpty()) {
                    all.append(sb.toString());
                    questions.append(sb.toString());
                }


                all.append(question.getAnswerHtml().replaceFirst("<span class=\"txt \">"
                        , "<span class=\"txt pdf-answer\">(답)<br></span><span class=\"txt \">"));
                answers.append(question.getAnswerHtml().replaceFirst("<span class=\"txt \">"
                        , "<span class=\"txt pdf-answer\">(답)<br></span><span class=\"txt \">"));
                all.append(question.getExplainHtml().replaceFirst("<span class=\"txt \">"
                        , "<span class=\"txt pdf-answer\">(해설)<br></span><span class=\"txt \">"));
                answers.append(question.getExplainHtml().replaceFirst("<span class=\"txt \">"
                        , "<span class=\"txt pdf-answer\">(해설)<br></span><span class=\"txt \">"));

            }

            map.put("all", all.toString().replace("<html>\n <head></head>\n <body>\n  ", "<div class=\"pdf-item\">")
                    .replace("\n </body>\n</html>", "</div>"));
            map.put("questions", questions.toString().replace("<html>\n <head></head>\n <body>\n  ", "<div class=\"pdf-item\">")
                    .replace("\n </body>\n</html>", "</div>"));
            map.put("answers", answers.toString().replace("<html>\n <head></head>\n <body>\n  ", "<div class=\"pdf-item\">")
                    .replace("\n </body>\n</html>", "</div>"));

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


    @Override
    public ResponseEntity<byte[]> HtmlToPdfExample(String html, String title) {

        // 변환할 HTML 파일 경로나, HTML 문자열을 준비합니다.
        String htmlFilePath = "path/to/input.html";
        String outputPdfPath = pdfDir;


        String htmlContent =
                "<html lang=\"ko\">" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\" />\n" +
                        "    <title>Title</title>\n" +
                        "    <link rel=\"stylesheet\" href=\"./inc/css/pdf.css\"></link>\n" +
                        "  <style>\n" +
                        "body {\n" +
                        "    margin: 0;\n" +
                        "    padding: 0;\n" +
                        "    font-family: \"Malgun Gothic\", sans-serif;\n" +
                        "}\n" +
                        "    @page {\n" +
                        "      margin: 10mm;\n" +
                        "      size: A4;\n" +
                        "    }\n" +
                        "    .header {\n" +
                        "    }\n" +
                        "    .content {\n" +
                        "      column-count: 2;\n" +
                        "      column-gap: 10mm;\n" +
                        "    }\n" +
                        ".content table, tr, td {\n" +
                        "  /* 컬럼 내부에서 분할을 허용 */\n" +
                        "  break-inside: auto !important;\n" +
                        "  -webkit-column-break-inside: auto !important;\n" +
                        "  page-break-inside: auto !important;\n" +
                        "}\n" +
                        "    span.txt {\n" +
                        "      break-inside: avoid !important;\n" +
                        "    }\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"header\">\n" +
                        "  <table class=\"pdf-title-table\">\n" +
                        "    <tr>\n" +
                        "      <td rowspan=\"2\" class=\"pdf-td-title\">" + title + "</td>\n" +
                        "      <td class=\"pdf-td-grade\"> &nbsp;&nbsp;&nbsp; 학년 &nbsp;&nbsp;반 &nbsp;&nbsp;번</td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "      <td class=\"pdf-td-name\">이름:</td>\n" +
                        "    </tr>\n" +
                        "  </table>\n" +
                        "</div>\n" +
                        "<div class=\"content\">\n" +
                        html +
                        "</div>\n" +
                        "</body>\n" +
                        "</html>";

        System.out.println("header = " + htmlContent);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();


            builder.withHtmlContent(convertTableToDiv(htmlContent.replace("type=\"text\">", "type=\"text\"/>")
                    .replace("<col style=\"width: 100%\">", "<col style=\"width: 100%\"/>")
                    .replace("&nbsp;", " ")
                    .replace("<br>", "<br/>")), "file:///D:/java7/QuestionBank/src/main/resources/static/");


            builder.toStream(baos);

            builder.useFont(
                    new File("C:/Windows/Fonts/malgun.ttf"),
                    "Malgun Gothic"
            );


            builder.run();

            byte[] pdfBytes = baos.toByteArray();

            // 3) HTTP 응답으로 PDF 바이너리 전달
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // 다운로드 되게 하고 싶다면 Content-Disposition 설정
            headers.setContentDispositionFormData("attachment", "myDocument.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    public String convertTableToDiv(String originalHtml) {
        // 1) HTML 문자열을 Jsoup Document로 파싱
        Document doc = Jsoup.parse(originalHtml);

        // 2) 모든 <table> 엘리먼트를 찾음
        Elements tables = doc.select("table");

        for (Element table : tables) {
            // 새로 만들 "table 대체"용 <div>
            Element tableWrapper = doc.createElement("div");
            tableWrapper.addClass("table-wrapper");
            // 예: CSS에서 .table-wrapper { column-count: 2; } 등 적용 가능

            // 3) 현재 <table> 안의 <tr>들을 순회
            Elements rows = table.select("tr");
            for (Element row : rows) {
                // <div class="row"> 형태로 만든다
                Element rowDiv = doc.createElement("div");
                rowDiv.addClass("row");

                // 4) 각 <tr> 안의 <td>들을 <div class="cell">로 변환
                Elements cells = row.select("td");
                for (Element cell : cells) {
                    Element cellDiv = doc.createElement("div");
                    cellDiv.addClass("cell");

                    // <td> 안의 내용(텍스트/태그 등)을 그대로 옮김
                    cellDiv.html(cell.html());

                    rowDiv.appendChild(cellDiv);
                }

                tableWrapper.appendChild(rowDiv);
            }

            // 5) 원래 <table>을 새로 만든 tableWrapper <div>로 교체
            table.replaceWith(tableWrapper);
        }

        // 6) 변환된 DOM을 문자열로 직렬화
        return doc.html();
    }

}
