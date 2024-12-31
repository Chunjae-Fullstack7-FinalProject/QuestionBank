package net.questionbank.service.textbook;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.dto.main.MainDTO;

import net.questionbank.service.textbook.TextbookServiceIf;
import net.questionbank.dto.textbook.TextBookApiDTO;
import net.questionbank.dto.textbook.TextbookApiResponse;
import net.questionbank.dto.textbook.TextbookDTO;
import net.questionbank.exception.CustomRuntimeException;
import net.questionbank.mapper.TextbookMapper;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class TextbookServiceImpl implements TextbookServiceIf{
    private final TextbookMapper textbookMapper;
    private final WebClient webClient;
    @Override
    public List<MainDTO> mainList(String subjectId) {
        List<String> authorList = textbookMapper.authorListBySubject(subjectId);
        List<MainDTO> mainList = new ArrayList<>();
        for(String author : authorList) {
            MainDTO mainDTO = new MainDTO();
            List<TextbookDTO> textbooks = textbookMapper.textbookListsByAuthor(author);
            mainDTO.setAuthorName(author);
            mainDTO.setTextbooks(textbooks);
            mainList.add(mainDTO);
        }
        return mainList;
    }

    @Override
    public TextBookApiDTO getTextbookDetails(String subjectId) {
        try {
            Mono<TextbookApiResponse> textbookApiResponseMono = webClient.post()
                    .uri("/chapter/subjectInfo-list")
                    .bodyValue(Map.of("subjectId",subjectId))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, res -> Mono.error(new CustomRuntimeException("교과서정보 조회 중 에러 발생, code : " + res.statusCode())))
                    .bodyToMono(TextbookApiResponse.class);
            TextbookApiResponse textbookApiResponse = textbookApiResponseMono.block();
            if(textbookApiResponse == null) {
                throw new CustomRuntimeException("교과서정보 조회 중 에러 발생");
            }
            List<TextBookApiDTO> textbookDetailDTOList = textbookApiResponse.getSubjectInfoList();
            if(textbookDetailDTOList == null) {
                return null;
            }
            return textbookDetailDTOList.get(0);
        }catch(Exception e) {
            log.error(e.getMessage());
            throw new CustomRuntimeException("교과서정보 조회 중 에러 발생");
        }
    }

}
