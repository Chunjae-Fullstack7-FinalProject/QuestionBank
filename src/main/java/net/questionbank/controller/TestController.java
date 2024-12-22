package net.questionbank.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.service.step3.Step3Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/customExam")
public class TestController {
    private final Step3Service step3Service;
    private final SpringTemplateEngine templateEngine;

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
