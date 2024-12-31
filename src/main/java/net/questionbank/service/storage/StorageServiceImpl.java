package net.questionbank.service.storage;

import lombok.RequiredArgsConstructor;
import net.questionbank.domain.Subject;
import net.questionbank.dto.test.TestDTO;
import net.questionbank.dto.test.TestSearchDTO;
import net.questionbank.dto.textbook.TextbookDTO;
import net.questionbank.mapper.TestMapper;
import net.questionbank.repository.TextbookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageServiceIf {
    private final TestMapper testMapper;
    private final TextbookRepository textbookRepository;

    @Override
    public List<TestDTO> getTestList(TestSearchDTO testSearchDTO) {
        if(testSearchDTO.getSubject() != null && testSearchDTO.getSubject().equals("0"))
            testSearchDTO.setSubject(null);
        if(testSearchDTO.getTextbookId() != null && testSearchDTO.getTextbookId() == 0) {
            testSearchDTO.setTextbookId(null);
        }
        return testMapper.findTestList(testSearchDTO);
    }

    @Override
    public List<TextbookDTO> getTextbookBySubjectId(String subjectId) {
        return textbookRepository.findAllBySubject(Subject.builder().subjectId(subjectId).build())
                .stream().map(value -> TextbookDTO.builder()
                        .textbookId(value.getTextbookId())
                        .title(value.getTitle())
                        .author(value.getAuthor())
                        .subjectId(value.getSubject().getSubjectId())
                        .build()
        ).toList();
    }
}
