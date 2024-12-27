package net.questionbank.service.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import net.questionbank.annotation.RedirectWithError;
import net.questionbank.dto.presetExam.LargeChapterDTO;
import net.questionbank.dto.presetExam.PresetExamApiResponse;
import net.questionbank.dto.presetExam.PresetExamDTO;
import net.questionbank.dto.presetExam.PresetExamResponseDTO;
import net.questionbank.dto.question.QuestionApiDTO;
import net.questionbank.dto.question.QuestionPresetApiDTO;
import net.questionbank.dto.question.QuestionPresetRequestDTO;
import net.questionbank.dto.question.QuestionResponseDTO;
import net.questionbank.exception.CustomRuntimeException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Logging
@Log4j2
@RequiredArgsConstructor
public class TestServiceImpl implements TestServiceIf {
    private final WebClient webClient;
    //교재코드로 대단원별 세팅된 시험지 리스트 가져오기
    @Override
    public Mono<PresetExamApiResponse> getPresetExam(String subjectId){
        try {
            return webClient.post()
                    .uri("/chapter/exam-list")
                    .bodyValue(Map.of("subjectId",subjectId))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, res -> Mono.error(new CustomRuntimeException("시험지정보 조회 중 에러 발생, code : " + res.statusCode())))
                    .bodyToMono(PresetExamApiResponse.class);
        }catch(Exception e) {
            log.error(e.getMessage());
            throw new CustomRuntimeException("시험지정보 조회 중 에러 발생");
        }
    }
    //과목별 대단원리스트 + 대단원별 시험지 리스트 가져오기
    @Override
    public List<LargeChapterDTO> getPresetExamList(String subjectId) {
        try{
            Mono<PresetExamApiResponse> presetExamApiResponseMono = webClient.post()
                    .uri("/chapter/exam-list")
                    .bodyValue(Map.of("subjectId",subjectId))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, res -> Mono.error(new CustomRuntimeException("시험지정보 조회 중 에러 발생, code : " + res.statusCode())))
                    .bodyToMono(PresetExamApiResponse.class);
            PresetExamApiResponse presetExamApiResponse = presetExamApiResponseMono.block();

            if(presetExamApiResponse == null) {
                throw new CustomRuntimeException("시험지 정보 없음");
            }

            List<PresetExamResponseDTO> presetExamResponseDTOList = presetExamApiResponse.getExamList();

            if(presetExamResponseDTOList == null || presetExamResponseDTOList.isEmpty()) {
                throw new CustomRuntimeException("시험지 정보 없음");
            }

            List<LargeChapterDTO> largeChapterDTOList = new ArrayList<>();

            presetExamResponseDTOList.forEach(presetExamResponseDTO -> {
                LargeChapterDTO largeChapterDTO = LargeChapterDTO.builder()
                        .largeChapterId(presetExamResponseDTO.getLargeChapterId())
                        .largeChapterName(presetExamResponseDTO.getLargeChapterName())
                        .presetExams(
                                new ArrayList<>(
                                    List.of(
                                           PresetExamDTO.builder()
                                                .examId(presetExamResponseDTO.getExamId())
                                                .examName(presetExamResponseDTO.getExamName())
                                                .itemCnt(presetExamResponseDTO.getItemCnt())
                                                .build()
                                    )
                                )
                        )
                        .build();
                if(!largeChapterDTOList.contains(largeChapterDTO)) {
                    largeChapterDTOList.add(largeChapterDTO);
                }else{
                    int index = largeChapterDTOList.indexOf(largeChapterDTO);
                    largeChapterDTOList.get(index).getPresetExams().add(
                                PresetExamDTO.builder()
                                        .examId(presetExamResponseDTO.getExamId())
                                        .examName(presetExamResponseDTO.getExamName())
                                        .itemCnt(presetExamResponseDTO.getItemCnt())
                                        .build()
                    );
                }
            });
            largeChapterDTOList.sort(Comparator.comparingInt(LargeChapterDTO::getLargeChapterId));
            return largeChapterDTOList;
        }catch(Exception e) {
            log.error(e.getMessage());
            throw new CustomRuntimeException("시험지 정보 조회 중 오류 발생");
        }
    }

    @Override
    public String[] getPresetExamQuestions(String[] examIds) {
        try{
            QuestionResponseDTO<QuestionPresetApiDTO> questionResponseDTO = getPresetExamQuestionsFromApi(examIds).block();
            if(questionResponseDTO == null) {
                throw new CustomRuntimeException("세팅지 문제 조회 실패 : 조회된 문제가 없음");
            }
            List<QuestionPresetApiDTO> itemList = questionResponseDTO.getItemList();
            return longListToStringArray(
                    itemList.stream().map(QuestionApiDTO::getItemId).toList()
            );
        }catch(Exception e) {
            log.error(e.getMessage());
        }

        return new String[0];
    }
    private Mono<QuestionResponseDTO<QuestionPresetApiDTO>> getPresetExamQuestionsFromApi(String [] examIds) {
        QuestionPresetRequestDTO requestDTO = QuestionPresetRequestDTO.builder()
                .examIdList(stringArrayToLongList(examIds))
                .build();
        try{
            return  webClient.post()
                    .uri("/item-img/exam-list/item-list")
                    .bodyValue(requestDTO)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, res -> Mono.error(new CustomRuntimeException("문제은행 서버에서 시험지 정보 조회중 에러 발생, code : " + res.statusCode())))
                    .bodyToMono(new ParameterizedTypeReference<QuestionResponseDTO<QuestionPresetApiDTO>>() {});
        }catch(Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
    private List<Long> stringArrayToLongList(String[] stringArray) {
        List<Long> longList = new ArrayList<>();
        for(String string : stringArray) {
            longList.add(Long.parseLong(string));
        }
        return longList;
    }
    private String[] longListToStringArray(List<Long> longList) {
        String[] stringArray = new String[longList.size()];
        for(int i = 0; i < longList.size(); i++) {
            stringArray[i] = String.valueOf(longList.get(i));
        }
        return stringArray;
    }
}
