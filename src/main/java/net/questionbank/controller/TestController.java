package net.questionbank.controller;

import jakarta.servlet.http.HttpSession;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;

import net.questionbank.annotation.RedirectWithError;
import net.questionbank.dto.presetExam.LargeChapterDTO;
import net.questionbank.dto.textbook.TextBookApiDTO;
import net.questionbank.exception.CustomRuntimeException;
import net.questionbank.service.step3.Step3Service;
import net.questionbank.service.test.TestServiceIf;
import net.questionbank.service.textbook.TextbookServiceIf;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@Logging
@RequestMapping("/customExam")
public class TestController {
    private final Step3Service step3Service;
    private final TextbookServiceIf textbookService;
    private final TestServiceIf testService;

    @PostMapping("/step0")
    @RedirectWithError(redirectUri = "/error/error")
    public String step0(@RequestParam String subjectId, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        log.info(subjectId);
        TextBookApiDTO textbookDetailDTO = textbookService.getTextbookDetails(subjectId);
        if (textbookDetailDTO == null) {
            throw new CustomRuntimeException("textbook not found");
        }
        session.setAttribute("textbookDetailDTO", textbookDetailDTO);
        model.addAttribute("textbookDetailDTO", textbookDetailDTO);
        List<LargeChapterDTO> largeChapterList = testService.getPresetExamList(subjectId);
        if (largeChapterList == null) {
            throw new CustomRuntimeException("presetExam not found");
        }
        model.addAttribute("largeChapterList", largeChapterList);
        log.info("largeChapterList : {}", largeChapterList);
        return "test/step0";
    }


    /*
    문항 편집(문제 목록, 문제지요약)
    step1에서 받은 과목 id 값
    step2/sub03_01_01로 리턴
     */
    @GetMapping("/step2")
    public String getItemIds(Model model, @RequestParam(required = false, defaultValue = "") String type) {
        String[] questionIds = {"494519", "494552"
                , "487868"
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
                , "487866"
                , "487867"
                , "493179"};
        model.addAttribute("questionIds", questionIds);
        model.addAttribute("type", type);
        return "test/step2";
    }

    @GetMapping("/step3")
    @PostMapping("/step3")
    public String step3(Model model, @RequestParam(required = false) List<Long> itemIdList, @RequestParam(required = false) Long subjectId) {
        int testId = 1;
        List<Long> ids = new ArrayList<>();
        ids.add(494519L);
        ids.add(494552L);
        ids.add(494553L);
        ids.add(493138L);
        ids.add(493140L);
        ids.add(493137L);
        ids.add(493139L);
        ids.add(493141L);
        ids.add(487792L);
        ids.add(494581L);

        Long testSubjectId = 1154L;

        model.addAttribute("testInfo", step3Service.testInfo(itemIdList == null ? ids : itemIdList, subjectId == null ? testSubjectId : subjectId));
        model.addAttribute("subjectId", subjectId == null ? testSubjectId : subjectId);
        model.addAttribute("itemIdList", itemIdList == null ? ids : itemIdList);
        return "test/sub04_01";
    }

    @GetMapping("/complete")
    @PostMapping("/complete")
    public String complete() {
        return "test/sub04_02";
    }

    @GetMapping("/pdf")
    public String step3() {
        return "test/test";
    }
}
