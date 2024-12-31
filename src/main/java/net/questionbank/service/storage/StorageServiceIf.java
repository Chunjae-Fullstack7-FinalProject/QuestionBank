package net.questionbank.service.storage;

import net.questionbank.dto.test.TestDTO;
import net.questionbank.dto.test.TestSaveDTO;
import net.questionbank.dto.test.TestSearchDTO;
import net.questionbank.dto.textbook.TextbookDTO;

import java.util.List;

public interface StorageServiceIf {
    List<TestDTO> getTestList(TestSearchDTO testSearchDTO);
    List<TextbookDTO> getTextbookBySubjectId(String subjectId);
}
