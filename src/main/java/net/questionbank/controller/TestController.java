package net.questionbank.controller;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import net.questionbank.annotation.RedirectWithError;
import net.questionbank.dto.presetExam.LargeChapterDTO;
import net.questionbank.dto.presetExam.PresetExamApiResponse;
import net.questionbank.dto.textbook.TextbookApiResponse;
import net.questionbank.dto.textbook.TextbookDetailDTO;
import net.questionbank.exception.CustomRuntimeException;
import net.questionbank.service.test.TestServiceIf;
import net.questionbank.service.textbook.TextbookServiceIf;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Log4j2
@Logging
@RequestMapping("/customExam")
public class TestController {
    private final TextbookServiceIf textbookService;
    private final TestServiceIf testService;
    @PostMapping("/step0")
    @RedirectWithError(redirectUri = "/error/error")
    public String step0(@RequestParam String subjectId, Model model) {
        log.info(subjectId);
        TextbookDetailDTO textbookDetailDTO = textbookService.getTextbookDetails(subjectId);
        if(textbookDetailDTO == null) {
            throw new CustomRuntimeException("textbook not found");
        }
        model.addAttribute("textbookDetailDTO", textbookDetailDTO);
        List<LargeChapterDTO> largeChapterList = testService.getPresetExamList(subjectId);
        if(largeChapterList == null) {
            throw new CustomRuntimeException("presetExam not found");
        }
        model.addAttribute("largeChapterList", largeChapterList);
        log.info("largeChapterList : {}", largeChapterList);
        return "test/step0";
    }
}
