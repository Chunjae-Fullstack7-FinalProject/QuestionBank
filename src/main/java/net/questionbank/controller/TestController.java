package net.questionbank.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import net.questionbank.domain.Subject;
import net.questionbank.dto.main.SubjectRequestDTO;
import net.questionbank.dto.test.LargeDTO;
import net.questionbank.service.test.TestServiceIf;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Logging
@Log4j2
@RequestMapping("/customExam")
public class TestController {
    private final TestServiceIf testService;

    @GetMapping("/step1")
    public String step1(Model model, SubjectRequestDTO subjectRequestDTO) {
        subjectRequestDTO.setSubjectId("1154");
        List<LargeDTO> largeList = testService.step1(subjectRequestDTO);
        model.addAttribute("largeList", largeList);
        model.addAttribute("subjectId", "1154");
        return "test/step1";
    }
}