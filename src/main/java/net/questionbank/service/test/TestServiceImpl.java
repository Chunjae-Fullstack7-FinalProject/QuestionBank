package net.questionbank.service.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import net.questionbank.annotation.RedirectWithError;
import net.questionbank.dto.presetExam.LargeChapterDTO;
import net.questionbank.dto.presetExam.PresetExamApiResponse;
import net.questionbank.dto.presetExam.PresetExamDTO;
import net.questionbank.dto.presetExam.PresetExamResponseDTO;
import net.questionbank.exception.CustomRuntimeException;
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
}
