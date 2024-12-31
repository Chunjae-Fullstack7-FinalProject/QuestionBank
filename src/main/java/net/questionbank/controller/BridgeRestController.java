package net.questionbank.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@Log4j2
@Logging
@RequestMapping("/bridge")
public class BridgeRestController {
    private final WebClient webClient;
    @RequestMapping (value = "/t-sherpa/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE })
    public Mono<ResponseEntity<String>> bridgeToTsherpa(
            HttpServletRequest request,
            @RequestBody(required = false) String body,
            @RequestHeader HttpHeaders headers
    ) {
//        log.info("requestBody:{}",body); //리퀘스트바디 로그. 필요하면 쓰셈
        // 프록시할 실제 경로 추출
        String path = request.getRequestURI().replace("/bridge/t-sherpa", "");

        // WebClient 요청 생성
        WebClient.RequestBodySpec requestBodySpec = webClient
                .method(HttpMethod.valueOf(request.getMethod()))
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON);

        // 헤더 복사 (필요한 헤더만 선택적으로)
        headers.forEach((key, values) -> {
            if (!key.equalsIgnoreCase("host")) { // host 헤더는 제외
                requestBodySpec.header(key, values.toArray(new String[0]));
            }
        });

        // body가 있는 경우에만 body 추가
        WebClient.RequestHeadersSpec<?> requestSpec = body != null ? requestBodySpec.bodyValue(body)
                : requestBodySpec;

        // 요청 실행 및 응답 반환
        return requestSpec.exchangeToMono(response -> {
            return response.bodyToMono(String.class)
                    .map(responseBody -> ResponseEntity
                            .status(response.statusCode())
                            .headers(response.headers().asHttpHeaders())
                            .body(responseBody));
        });
    }
}
