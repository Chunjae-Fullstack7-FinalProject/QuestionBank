package net.questionbank.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import net.questionbank.annotation.RedirectWithError;
import net.questionbank.dto.question.QuestionHtmlApiDTO;
import net.questionbank.service.step3.Step3Service;
import net.questionbank.service.test.TestServiceIf;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Logging
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/tests")
public class TestRestController {
    private final Step3Service step3Service;
    private final TestServiceIf testService;
    @RedirectWithError(redirectUri = "/error/error")
    @GetMapping("/preset-tests/{exam-id}/pdf-html")
    public ResponseEntity<Map<String,?>> getPresetExamPdf(@PathVariable("exam-id") String examId){
        try{
            List<Long> itemIdList = testService.getPresetExamItemList(examId);
            if(itemIdList == null || itemIdList.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","해당하는 문항정보가 없습니다."));
            }
            List<QuestionHtmlApiDTO> questionHtmlApiDTOList = step3Service.getQuestionsHtmlFromApi(itemIdList);
            Map<String, List<String>> pdfHtml = step3Service.testPdfHtmlList(questionHtmlApiDTOList);
            if(pdfHtml == null || pdfHtml.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","해당하는 문항정보가 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.OK).body(pdfHtml);
        }catch(Exception e){
            log.error(e);
            return ResponseEntity.internalServerError().body(Map.of("message","일시적 오류입니다. 다시시도하세요."));
        }
    }
}
