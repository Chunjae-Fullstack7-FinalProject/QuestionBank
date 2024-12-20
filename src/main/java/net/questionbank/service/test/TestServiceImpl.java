package net.questionbank.service.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

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
@Log4j2
public class TestServiceImpl implements TestServiceIf {

    private final WebClient webClient;



    public List<LargeDTO> step1(SubjectRequestDTO subjectRequestDTO) {
        // WebClient로 POST 요청을 보내고 응답을 String으로 받음
        ApiResponseDTO response = webClient.post()
                .uri("https://tsherpa.item-factory.com/chapter/chapter-list")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(subjectRequestDTO)
                .retrieve()
                .bodyToMono(ApiResponseDTO.class)  // 응답을 String 형식으로 처리
                .block();

        log.info("Received response: {}", response);

        if (response.getChapterList() != null) {
            // TreeMap 사용하여 largeChapterId 기준으로 정렬
            Map<Long, LargeDTO> largeMap = Collections.synchronizedMap(new TreeMap<>());

            // 캐시를 위한 ConcurrentHashMap
            Map<String, ApiResponseItemCountDTO> cache = new ConcurrentHashMap<>();

            // 병렬 처리 및 캐싱된 데이터를 활용
            List<Mono<?>> monoList = response.getChapterList().stream()
                    .map(step1DTO -> {
                        String cacheKey = step1DTO.getSmallChapterId() + "-" + step1DTO.getTopicChapterId();
                        ApiResponseItemCountDTO responseItemCount = cache.get(cacheKey);

                        // 캐시가 없으면 API 호출하여 결과를 캐시
                        if (responseItemCount == null) {
                            Step1DTO requestDTO = Step1DTO.builder()
                                    .curriculumCode(step1DTO.getCurriculumCode())
                                    .subjectId(step1DTO.getSubjectId())
                                    .largeChapterId(step1DTO.getLargeChapterId())
                                    .mediumChapterId(step1DTO.getMediumChapterId())
                                    .smallChapterId(step1DTO.getSmallChapterId())
                                    .build();

                            // 비동기 방식으로 API 호출하고 응답을 받음
                            Mono<ApiResponseItemCountDTO> apiResponseMono = webClient.post()
                                    .uri("https://tsherpa.item-factory.com/item-img/chapters/item-count")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(requestDTO)
                                    .retrieve()
                                    .bodyToMono(ApiResponseItemCountDTO.class);
//                                    .doOnTerminate(() -> log.info("Completed API call for smallChapterId: {}", step1DTO.getSmallChapterId()));

                            // API 응답을 처리하고, 결과를 캐시
                            apiResponseMono.subscribe(responseFromApi -> {
                                cache.put(cacheKey, responseFromApi);
                                processApiResponse(responseFromApi, step1DTO, largeMap);
                            });

                            return apiResponseMono.then();
                        } else {
                            processApiResponse(responseItemCount, step1DTO, largeMap);
                            return Mono.empty(); // 이미 처리된 경우 빈 Mono 반환
                        }
                    })
                    .collect(Collectors.toList());

            // 병렬 처리를 위한 모노 리스트를 실행하고, 완료될 때까지 기다림
            Mono.when(monoList).block(); // .block()을 호출하여 모든 Mono 작업이 완료될 때까지 대기

            // TreeMap에서 LargeDTO를 모두 가져와 largeDTOList에 추가
            List<LargeDTO> largeDTOList = new ArrayList<>(largeMap.values());

            // 각 계층을 정렬하기 위해 Comparator 사용
            largeDTOList.sort(Comparator.comparing(LargeDTO::getLargeChapterName)); // 대단원 이름 기준 정렬

            for (LargeDTO largeDTO : largeDTOList) {
                largeDTO.getMediumList().sort(Comparator.comparing(MediumDTO::getMediumChapterName)); // 중단원 이름 기준 정렬

                for (MediumDTO mediumDTO : largeDTO.getMediumList()) {
                    mediumDTO.getSmallList().sort(Comparator.comparing(SmallDTO::getSmallChapterName)); // 소단원 이름 기준 정렬

                    for (SmallDTO smallDTO : mediumDTO.getSmallList()) {
                        smallDTO.getTopicList().sort(Comparator.comparing(TopicDTO::getTopicChapterName)); // 토픽 이름 기준 정렬
                    }
                }
            }

            log.info("largeDTOList:{}", largeDTOList);
            return largeDTOList;
        } else {
            return null;
        }
    }


    private void processApiResponse(ApiResponseItemCountDTO responseItemCount, Step1DTO step1DTO, Map<Long, LargeDTO> largeMap) {
        Integer smallItemCount = 0;
        Integer topicItemCount = 0;

        // itemCount 정보를 smallItemCount와 topicItemCount에 반영
        if (responseItemCount != null) {
            // SmallDTO의 문항 수 반영
            for (ApiResponseItemCountDTO.SmallItemCount small : responseItemCount.getListSmallItemCount()) {
                if (small.getSmallChapterId().equals(step1DTO.getSmallChapterId())) {
                    smallItemCount = small.getItemCount();
                    break;
                }
            }

            // TopicDTO의 문항 수 반영
            for (ApiResponseItemCountDTO.TopicItemCount topic : responseItemCount.getListTopicItemCount()) {
                if (topic.getTopicChapterId().equals(step1DTO.getTopicChapterId())) {
                    topicItemCount = topic.getItemCount();
                    break;
                }
            }
        }

        // LargeDTO 객체 처리 (동기화)
        synchronized (largeMap) {
            LargeDTO largeDTO = largeMap.computeIfAbsent(step1DTO.getLargeChapterId(), id -> {
                LargeDTO newLargeDTO = new LargeDTO();
                newLargeDTO.setLargeChapterId(step1DTO.getLargeChapterId());
                newLargeDTO.setLargeChapterName(step1DTO.getLargeChapterName());
                newLargeDTO.setMediumList(new ArrayList<>());
                return newLargeDTO;
            });

            // MediumDTO 처리 (동기화)
            synchronized (largeDTO) {
                MediumDTO mediumDTO = findOrCreateMediumDTO(largeDTO, step1DTO);

                // SmallDTO 처리 (동기화)
                synchronized (mediumDTO) {
                    SmallDTO smallDTO = findOrCreateSmallDTO(mediumDTO, step1DTO);

                    // SmallDTO에 itemCount 바인딩
                    smallDTO.setItemCount(smallItemCount);

                    // TopicDTO 처리 및 itemCount 바인딩 (동기화)
                    synchronized (smallDTO) {
                        TopicDTO topicDTO = findOrCreateTopicDTO(smallDTO, step1DTO);
                        topicDTO.setItemCount(topicItemCount);
                    }
                }
            }
        }
    }

    private MediumDTO findOrCreateMediumDTO(LargeDTO largeDTO, Step1DTO step1DTO) {
        // MediumDTO를 찾거나 없으면 새로 생성
        for (MediumDTO mediumDTO : largeDTO.getMediumList()) {
            if (mediumDTO.getMediumChapterId().equals(step1DTO.getMediumChapterId())) {
                return mediumDTO;
            }
        }
        // MediumDTO가 없으면 새로 생성하고 리스트에 추가
        MediumDTO newMediumDTO = new MediumDTO();
        newMediumDTO.setMediumChapterId(step1DTO.getMediumChapterId());
        newMediumDTO.setMediumChapterName(step1DTO.getMediumChapterName());
        newMediumDTO.setSmallList(new ArrayList<>());
        largeDTO.getMediumList().add(newMediumDTO);
        return newMediumDTO;
    }

    private SmallDTO findOrCreateSmallDTO(MediumDTO mediumDTO, Step1DTO step1DTO) {
        // SmallDTO를 찾거나 없으면 새로 생성
        for (SmallDTO smallDTO : mediumDTO.getSmallList()) {
            if (smallDTO.getSmallChapterId().equals(step1DTO.getSmallChapterId())) {
                return smallDTO;
            }
        }
        // SmallDTO가 없으면 새로 생성하고 리스트에 추가
        SmallDTO newSmallDTO = new SmallDTO();
        newSmallDTO.setSmallChapterId(step1DTO.getSmallChapterId());
        newSmallDTO.setSmallChapterName(step1DTO.getSmallChapterName());
        newSmallDTO.setTopicList(new ArrayList<>());
        mediumDTO.getSmallList().add(newSmallDTO);
        return newSmallDTO;
    }

    private TopicDTO findOrCreateTopicDTO(SmallDTO smallDTO, Step1DTO step1DTO) {
        // TopicDTO를 찾거나 없으면 새로 생성
        for (TopicDTO topicDTO : smallDTO.getTopicList()) {
            if (topicDTO.getTopicChapterId().equals(step1DTO.getTopicChapterId())) {
                return topicDTO;
            }
        }
        // TopicDTO가 없으면 새로 생성하고 리스트에 추가
        TopicDTO newTopicDTO = new TopicDTO();
        newTopicDTO.setTopicChapterId(step1DTO.getTopicChapterId());
        newTopicDTO.setTopicChapterName(step1DTO.getTopicChapterName());
        smallDTO.getTopicList().add(newTopicDTO);
        return newTopicDTO;
    }






}
