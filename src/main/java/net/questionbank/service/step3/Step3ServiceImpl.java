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
import net.questionbank.dto.textbook.TextBookApiDTO;
import net.questionbank.dto.textbook.TextBookRequestDTO;
import net.questionbank.dto.textbook.TextBookResponseDTO;
import net.questionbank.mapper.QuestionMapper;
import net.questionbank.mapper.TestMapper;
import net.questionbank.repository.QuestionRepository;
import net.questionbank.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
public class Step3ServiceImpl implements Step3Service {
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final WebClient webClient;
    private final QuestionMapper questionMapper;
    private final TestMapper testMapper;

    @Override
    public TempTestDTO testInfo(List<Long> itemIdList, Long subjectId) {
//        Test test = testRepository.findById(testId).orElse(null);
//        if(test == null) {
//            return null;
//        }
        TempTestDTO tempTestDTO = TempTestDTO.builder()
//                .title(test.getTitle())
//                .member(test.getMember())
//                .createdAt(test.getCreatedAt())
//                .textbook(test.getTextbook())
//                .testId(test.getTestId())
                .questions(getQuestionsFromApi(itemIdList))
//                .textbook(test.getTextbook())
                .textbookApiDTO(getTextBookFromApi(subjectId))
                .build();
        return tempTestDTO;
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
                .bodyValue(TextBookRequestDTO.builder().subjectId((long)subjectId).build())
                .retrieve()
                .bodyToMono(TextBookResponseDTO.class).block();

        if(responseDTO == null || !responseDTO.getSuccessYn().equals("Y")) return null;

        if(responseDTO.getSubjectInfoList().isEmpty()) return null;

        return responseDTO.getSubjectInfoList().get(0);
    }

    @Override
    @Transactional
    public void saveTestInfo(TestDTO testDTO, List<Long> questionIdList) {
        Test test = Test.builder()
                .title(testDTO.getTitle())
                .createdAt(LocalDateTime.now())
//                .member(Member.builder().memberId(testDTO.getUserId()).build())
                .textbook(Textbook.builder().textbookId(Integer.parseInt(String.valueOf(testDTO.getSubjectId()))).build())
                .build();
        testRepository.save(test);

        if(test.getTestId() == 0) {
            throw new IllegalStateException("저장 실패");
        }
        questionRepository.saveAll(questionIdList.stream().map(id -> Question.builder()
                .itemId(id.intValue())
                        .test(test)
                        .itemNo(questionIdList.indexOf(id)+1)
                        .build()
                ).toList()
        );
    }


}
