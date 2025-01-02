package net.questionbank.service.step3;

import net.questionbank.dto.question.QuestionImageApiDTO;
import net.questionbank.dto.question.QuestionHtmlApiDTO;
import net.questionbank.dto.test.TempTestHtmlDTO;
import net.questionbank.dto.test.TempTestImageDTO;
import net.questionbank.dto.test.TestSaveDTO;
import net.questionbank.dto.test.TestDataDTO;
import net.questionbank.dto.textbook.TextBookApiDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface Step3Service {
    TempTestImageDTO testInfoImage(List<Long> itemIdList, Long subjectId);
    TempTestHtmlDTO testInfoHtml(List<Long> itemIdList, Long subjectId);
    List<QuestionImageApiDTO> getQuestionsImageFromApi(List<Long> itemIdList);
    List<QuestionHtmlApiDTO> getQuestionsHtmlFromApi(List<Long> itemIdList);
    TextBookApiDTO getTextBookFromApi(Long subjectId);
    void saveTestInfo(TestSaveDTO testSaveDTO, List<Long> questionIdList);
    boolean sendTestInfo(TestDataDTO testDataDto);
    Map<String, List<String>> testPdfImageList(List<QuestionImageApiDTO> questionsFromApi);

    Map<String, String> testPdfImageStringList(List<QuestionImageApiDTO> questionsFromApi);

    Map<String, List<String>> testPdfHtmlList(List<QuestionHtmlApiDTO> questionsFromApi);

    Map<String, String> testPdfHtmlStringList(List<QuestionHtmlApiDTO> questionsFromApi);

    ResponseEntity<byte[]> HtmlToPdfExample(String html, String title);
}