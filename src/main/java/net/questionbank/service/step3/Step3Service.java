package net.questionbank.service.step3;

import net.questionbank.dto.QuestionDTO;
import net.questionbank.dto.TestDTO;
import net.questionbank.dto.TextBookDTO;

import java.util.List;

public interface Step3Service {
    TestDTO testInfo(int testId);
    List<QuestionDTO> getQuestionsFromApi(int testId);
    TextBookDTO getTextBookFromApi(int textBookId);
}