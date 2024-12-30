package net.questionbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import net.questionbank.annotation.RedirectWithError;
import net.questionbank.dto.main.SubjectRequestDTO;
import net.questionbank.dto.test.LargeDTO;
import net.questionbank.dto.presetExam.LargeChapterDTO;
import net.questionbank.dto.test.RequestBodyDTO;
import net.questionbank.dto.textbook.TextBookApiDTO;
import net.questionbank.exception.CustomRuntimeException;
import net.questionbank.service.step3.Step3Service;
import net.questionbank.service.test.TestServiceIf;
import net.questionbank.service.textbook.TextbookServiceIf;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Logging
@Log4j2
@RequestMapping("/customExam")
public class TestController {
    private final Step3Service step3Service;
    private final TextbookServiceIf textbookService;
    private final TestServiceIf testService;
    private final SpringTemplateEngine templateEngine;


   
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
  
    @GetMapping("/step1")
    public String step1(Model model, SubjectRequestDTO subjectRequestDTO, HttpSession session) {
        //여기 치고 들어오면 팅궈야 함.
        TextBookApiDTO textbookDetailDTO = (TextBookApiDTO)session.getAttribute("textbookDetailDTO");
        if (textbookDetailDTO == null) {
            throw new CustomRuntimeException("textbook not found");
        }
        String subjectId = textbookDetailDTO.getSubjectId().toString();
        subjectRequestDTO.setSubjectId(subjectId);
        List<LargeDTO> largeList = testService.step1(subjectRequestDTO);
        model.addAttribute("largeList", largeList);
        model.addAttribute("subjectId", subjectId);
        model.addAttribute("textbookDetailDTO", textbookDetailDTO);
        return "test/step1";
    }



    /*
    문항 편집(문제 목록, 문제지요약)
    step1에서 받은 과목 id 값
    step2/sub03_01_01로 리턴
     */
    @PostMapping("/step2")
    public String getItemIds(Model model,
                             @RequestParam(required = false, name = "examId") String[] examIds,
                             String[] questionIds,
                             @RequestParam(required = false, defaultValue = "") String type,
                             @RequestParam String strRequestBody,
                             @RequestParam String requestLow,
                             @RequestParam String requestMiddle,
                             @RequestParam String requestHigh) {

        if(examIds!=null){
            questionIds = testService.getPresetExamQuestions(examIds);
            model.addAttribute("questionIds", questionIds);
            model.addAttribute("type", "edit");
        }

        //json 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            RequestBodyDTO requestBodyDTO = objectMapper.readValue(strRequestBody, RequestBodyDTO.class);
            model.addAttribute("requestBodyDTO", requestBodyDTO); //재검색용 requestBody
        } catch(Exception e){
            log.error(e.getMessage());
        }
        model.addAttribute("requestLow", requestLow);
        model.addAttribute("requestMiddle", requestMiddle);
        model.addAttribute("requestHigh", requestHigh);
        model.addAttribute("type", type);
        return "test/step2";
    }


    //@GetMapping("/step3")
    @PostMapping("/step3")
    public String step3(Model model, @RequestParam(required = false, name="itemId") List<Long> itemIdList, HttpSession session) {
        TextBookApiDTO textbookDetailDTO = (TextBookApiDTO)session.getAttribute("textbookDetailDTO");
        log.info("itemIdList : {}", itemIdList);
        model.addAttribute("testInfo", step3Service.testInfoHtml(itemIdList, textbookDetailDTO.getSubjectId()));
        model.addAttribute("subjectId", textbookDetailDTO.getSubjectId());
        model.addAttribute("itemIdList", itemIdList);
        model.addAttribute("pdfFileId", UUID.randomUUID().toString());
        return "test/sub04_01";
    }

    @GetMapping("/complete")
    @PostMapping("/complete")
    public String complete() {
        return "test/sub04_02";
    }

    @GetMapping("/pdf")
    public String pdf(Model model) {
        List<Long> ids = new ArrayList<>();
//        ids.add(491164L);
//        ids.add(494519L);
//        ids.add(494552L);
//        ids.add(494553L);
//        ids.add(493138L);
//        ids.add(493140L);
//        ids.add(493137L);
//        ids.add(493139L);
//        ids.add(493141L);
//        ids.add(487792L);
//        ids.add(494581L);
//        ids.add(493497L);

        ids.add(481705L);
        ids.add(481709L);
        ids.add(481711L);
        ids.add(481712L);
        ids.add(481717L);
        ids.add(481718L);
        ids.add(977246L);
        ids.add(977249L);
        ids.add(977250L);
        ids.add(977253L);
        ids.add(977254L);
        ids.add(977257L);
        ids.add(977258L);
        ids.add(977259L);

        model.addAttribute("htmlList", step3Service.testPdfHtmlList(step3Service.getQuestionsHtmlFromApi(ids)));
        return "test/test2";
    }
}

