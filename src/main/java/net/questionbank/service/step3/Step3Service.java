package net.questionbank.service.step3;

import net.questionbank.dto.question.QuestionApiDTO;
import net.questionbank.dto.test.TempTestDTO;
import net.questionbank.dto.test.TestDTO;
import net.questionbank.dto.test.TestDataDTO;
import net.questionbank.dto.textbook.TextBookApiDTO;

import java.util.List;
import java.util.Map;

public interface Step3Service {
    TempTestDTO testInfo(List<Long> itemIdList, Long subjectId);
    List<QuestionApiDTO> getQuestionsFromApi(List<Long> itemIdList);
    TextBookApiDTO getTextBookFromApi(Long subjectId);
    void saveTestInfo(TestDTO testDTO, List<Long> questionIdList);
    boolean sendTestInfo(TestDataDTO testDataDto);

    Map<String, List<String>> testPdfImageList(List<QuestionApiDTO> questionsFromApi);
//    List<String> testPdfImageList(List<Long> itemIdList);
}