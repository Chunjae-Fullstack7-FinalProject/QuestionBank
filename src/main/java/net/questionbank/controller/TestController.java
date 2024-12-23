package net.questionbank.controller;

import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    /*
    문항 편집(문제 목록, 문제지요약)
    step1에서 받은 과목 id 값
    step2/sub03_01_01로 리턴
     */
    @GetMapping("/step2")
    public String getItemIds(Model model, @RequestParam(required = false, defaultValue = "noEdit") String type){
        String[] questionIds ={"494519", "494552"
                ,"487868"
                , "494553"
                , "493140"
                , "493137"
                , "493139"
                , "493141"
                , "487792"
                , "494581"
                , "494582"
                , "493122"
                , "493123"
                , "487816"
                , "494528"
                , "493138"
                ,"487866"
                ,"487867"
                ,"493179"};
        model.addAttribute("questionIds", questionIds);
        model.addAttribute("type", type);
    }

    @PostMapping("/step0")
    @RedirectWithError(redirectUri = "/error/error")
    public String step0(@RequestParam String subjectId, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        log.info(subjectId);
        TextbookDetailDTO textbookDetailDTO = textbookService.getTextbookDetails(subjectId);
        if(textbookDetailDTO == null) {
            throw new CustomRuntimeException("textbook not found");
        }
        session.setAttribute("textbookDetailDTO", textbookDetailDTO);
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
