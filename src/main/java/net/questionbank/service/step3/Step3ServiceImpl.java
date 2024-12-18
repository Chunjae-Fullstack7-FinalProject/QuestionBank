package net.questionbank.service.step3;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.domain.Test;
import net.questionbank.dto.*;
import net.questionbank.mapper.QuestionMapper;
import net.questionbank.repository.QuestionRepository;
import net.questionbank.repository.TestRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class Step3ServiceImpl implements Step3Service {
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final WebClient webClient;
    private final QuestionMapper questionMapper;

    @Override
    public TestDTO testInfo(int testId) {
        Test test = testRepository.findById(testId).orElse(null);
        if(test == null) {
            return null;
        }
        TestDTO testDTO = TestDTO.builder()
                .title(test.getTitle())
                .member(test.getMember())
                .createdAt(test.getCreatedAt())
                .textbook(test.getTextbook())
                .testId(test.getTestId())
                .questions(getQuestionsFromApi(test.getTestId()))
                .build();
        testDTO.setQuestions(getQuestionsFromApi(testId));
        return null;
    }

    @Override
    public List<QuestionDTO> getQuestionsFromApi(int testId) {
        QuestionListRequestDTO questionListRequestDTO = QuestionListRequestDTO.builder().itemIdList(questionMapper.findItemIdByTestId(testId)).build();
        return Objects.requireNonNull(webClient
                .post()
                .uri("/item-img/item-list")
                .bodyValue(questionListRequestDTO)
                .retrieve()
                .bodyToMono(QuestionListResponseDTO.class).block()).getItemList();
    }

    @Override
    public TextBookDTO getTextBookFromApi(int textBookId) {
        return null;
    }
}
