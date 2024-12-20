package net.questionbank.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.service.step3.Step3Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/customExam")
public class TestController {
    private final Step3Service step3Service;

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

        model.addAttribute("testInfo", step3Service.testInfo(itemIdList==null?ids:itemIdList, subjectId==null?testSubjectId:subjectId));
        model.addAttribute("subjectId", subjectId==null?testSubjectId:subjectId);
        model.addAttribute("itemIdList", itemIdList==null?ids:itemIdList);
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
