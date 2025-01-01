package net.questionbank.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.dto.MemberLoginDTO;
import net.questionbank.dto.test.TestSearchDTO;
import net.questionbank.dto.textbook.TextbookDTO;
import net.questionbank.service.storage.StorageServiceIf;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/storage")
public class StorageController {
    private final StorageServiceIf storageService;

    @Value("${file.pdfDir}")
    private String fileDir;


    @GetMapping("")
    public String storage(Model model, @Valid TestSearchDTO testSearchDTO, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes) {


        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/storage";
        }

        MemberLoginDTO loginDto = (MemberLoginDTO) session.getAttribute("loginDto");

        String userId = loginDto.getMemberId();

        testSearchDTO.setUserId(userId);

        model.addAttribute("testList", storageService.getTestList(testSearchDTO));
        model.addAttribute("searchInfo", testSearchDTO);


        List<TextbookDTO> textbookBySubjectId = (testSearchDTO.getSubject() != null && !testSearchDTO.getSubject().isEmpty()) ? storageService.getTextbookBySubjectId(testSearchDTO.getSubject()) : null;
        model.addAttribute("textbookList", textbookBySubjectId);

        return "main/storage";
    }


    @GetMapping("/download/{fileName}")
    public void pdfDownload(@PathVariable String fileName, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        File pdf = new File(fileDir + File.separator + fileName);

        try {
            byte[] file = FileUtils.readFileToByteArray(pdf);
            response.setContentType("applicatoin/octet-stream");
            response.setContentLength(file.length);
            response.setHeader("Content-Disposition","attachment; fileName=\""+ URLEncoder.encode(fileName, StandardCharsets.UTF_8)+"\";");
            response.setHeader("Content-Transfer-Encoding","binary");

            response.getOutputStream().write(file);
            response.getOutputStream().flush();
            response.getOutputStream().close();

        } catch (IOException e) {
            response.setContentType("text/html;charset=utf-8");
            try {
                response.getWriter().append("<script>alert('다시 시도해주세요.');</script>");
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
