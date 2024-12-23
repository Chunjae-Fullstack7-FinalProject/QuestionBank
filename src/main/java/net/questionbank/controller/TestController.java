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
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Log4j2
@Logging
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


    /*
    문항 편집(문제 목록, 문제지요약)
    step1에서 받은 과목 id 값
    step2/sub03_01_01로 리턴
     */
    @GetMapping("/step2")
    public String getItemIds(Model model, @RequestParam(required = false, defaultValue = "noEdit") String type) {
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
        model.addAttribute("pdfFileId", UUID.randomUUID().toString());
        return "test/sub04_01";
    }

    @GetMapping("/complete")
    @PostMapping("/complete")
    public String complete() {
        return "test/sub04_02";
    }

//    public void pdf() {
//        Document document = new Document(PageSize.A4, 30, 30, 30, 30);
//
//        try {
//            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream("test.pdf"));
//            pdfWriter.setInitialLeading(10f);
//            document.open();
//
//            CSSResolver cssResolver = new StyleAttrCSSResolver();
//            CssFile cssFile = null;
//        } catch (DocumentException | FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    @GetMapping("/pdf")
//    public String step3(Model model, HttpServletResponse response) {
//        List<Long> ids = new ArrayList<>();
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
//
//        Long testSubjectId = 1154L;
//
//        List<String> attributeValue = step3Service.testPdfImageList(ids);
//        model.addAttribute("testInfo", attributeValue);
////        model.addAttribute("testInfo", step3Service.testInfo(ids, testSubjectId));
//        return "test/test1";
//
////        String htmlContent = renderHtml("test/test1", model);
////
////        response.setCharacterEncoding("utf-8");
////        response.setContentType("application/pdf");
////        response.setHeader("Content-Disposition", "inline; filename=test.pdf");
////
////        try (OutputStream os = response.getOutputStream()) {
////            ITextRenderer renderer = new ITextRenderer();
////            renderer.getFontResolver().addFont("C:/Windows/Fonts/malgun.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
////            renderer.setDocumentFromString(htmlContent);
////            renderer.layout();
////            renderer.createPDF(os);
////        } catch (IOException e) {
////            log.info("pdf - error");
//////            throw new RuntimeException(e);
////        } catch (DocumentException e) {
////            log.info("pdf - error2");
////        }
//    }

//    private String renderHtml(String templateName, Model model) {
//        org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
//        model.asMap().forEach(context::setVariable);
//        return templateEngine.process(templateName, context);
//    }
}
