package net.questionbank.service.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.questionbank.annotation.Logging;
import net.questionbank.dto.main.SubjectRequestDTO;
import net.questionbank.dto.test.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Logging
@Log4j2
public class TestServiceImpl implements TestServiceIf {

    private final WebClient webClient;



    public List<LargeDTO> step1(SubjectRequestDTO subjectRequestDTO) {
    // WebClient로 POST 요청을 보내고 응답을 받아옴
        ApiResponseDTO response = webClient.post()
                .uri("https://tsherpa.item-factory.com/chapter/chapter-list")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(subjectRequestDTO)
                .retrieve()
                .bodyToMono(ApiResponseDTO.class)
                .block();


        if (response.getChapterList() != null) {
            // TreeMap 사용하여 largeChapterId 기준으로 정렬
            Map<Long, LargeDTO> largeMap = new TreeMap<>();

            // 응답 데이터를 반복 처리하여 LargeDTO 계층 구조 생성
            response.getChapterList().forEach(step1DTO -> {
                LargeDTO largeDTO = largeMap.computeIfAbsent(step1DTO.getLargeChapterId(), id -> {
                    LargeDTO newLargeDTO = new LargeDTO();
                    newLargeDTO.setLargeChapterId(step1DTO.getLargeChapterId());
                    newLargeDTO.setLargeChapterName(step1DTO.getLargeChapterName());
                    newLargeDTO.setMediumList(new ArrayList<>());
                    return newLargeDTO;
                });

                MediumDTO mediumDTO = largeDTO.getMediumList().stream()
                        .filter(m -> m.getMediumChapterId().equals(step1DTO.getMediumChapterId()))
                        .findFirst()
                        .orElseGet(() -> {
                            MediumDTO newMediumDTO = new MediumDTO();
                            newMediumDTO.setMediumChapterId(step1DTO.getMediumChapterId());
                            newMediumDTO.setMediumChapterName(step1DTO.getMediumChapterName());
                            newMediumDTO.setSmallList(new ArrayList<>());
                            largeDTO.getMediumList().add(newMediumDTO);
                            return newMediumDTO;
                        });

                SmallDTO smallDTO = mediumDTO.getSmallList().stream()
                        .filter(s -> s.getSmallChapterId().equals(step1DTO.getSmallChapterId()))
                        .findFirst()
                        .orElseGet(() -> {
                            SmallDTO newSmallDTO = new SmallDTO();
                            newSmallDTO.setSmallChapterId(step1DTO.getSmallChapterId());
                            newSmallDTO.setSmallChapterName(step1DTO.getSmallChapterName());
                            newSmallDTO.setTopicList(new ArrayList<>());
                            mediumDTO.getSmallList().add(newSmallDTO);
                            return newSmallDTO;
                        });

                TopicDTO topicDTO = smallDTO.getTopicList().stream()
                        .filter(t -> t.getTopicChapterId().equals(step1DTO.getTopicChapterId()))
                        .findFirst()
                        .orElseGet(() -> {
                            TopicDTO newTopicDTO = new TopicDTO();
                            newTopicDTO.setTopicChapterId(step1DTO.getTopicChapterId());
                            newTopicDTO.setTopicChapterName(step1DTO.getTopicChapterName());
                            smallDTO.getTopicList().add(newTopicDTO);
                            return newTopicDTO;
                        });
            });

            // TreeMap에서 LargeDTO를 모두 가져와 largeDTOList에 추가
            List<LargeDTO> largeDTOList = new ArrayList<>(largeMap.values());

            // 각 계층을 정렬하기 위해 Comparator 사용
            largeDTOList.sort(Comparator.comparing(LargeDTO::getLargeChapterId)); // 대단원 이름 기준 정렬

            for (LargeDTO largeDTO : largeDTOList) {
                largeDTO.getMediumList().sort(Comparator.comparing(MediumDTO::getMediumChapterId)); // 중단원 이름 기준 정렬

                for (MediumDTO mediumDTO : largeDTO.getMediumList()) {
                    mediumDTO.getSmallList().sort(Comparator.comparing(SmallDTO::getSmallChapterId)); // 소단원 이름 기준 정렬

                    for (SmallDTO smallDTO : mediumDTO.getSmallList()) {
                        smallDTO.getTopicList().sort(Comparator.comparing(TopicDTO::getTopicChapterId)); // 토픽 이름 기준 정렬
                    }
                }
            }

            return largeDTOList;
        } else {
            return null;
        }
    }
}
