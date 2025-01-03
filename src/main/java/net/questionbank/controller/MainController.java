package net.questionbank.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import net.questionbank.dto.MemberLoginDTO;
import net.questionbank.dto.main.MainDTO;
import net.questionbank.dto.main.ResponseDTO;
import net.questionbank.dto.main.SubjectRequestDTO;
import net.questionbank.service.textbook.TextbookServiceIf;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Logging
@Log4j2
@RequestMapping("/")
public class MainController {
    private final TextbookServiceIf textbookService;
    @RequestMapping
    public String index(){
        return "redirect:/main";
    }

    @GetMapping(value = {"/main","/"})
    public String mainPage(HttpSession session){
        MemberLoginDTO loginDTO = (MemberLoginDTO) session.getAttribute("loginDto");
        return "main/main";
    }

    @PostMapping("/api/mainList")
    @ResponseBody
    public ResponseEntity<?> getMainList(@RequestBody SubjectRequestDTO subjectRequest) {
        // 과목 ID 받아오기
        String subjectId = subjectRequest.getSubjectId();

        // 해당 과목에 맞는 데이터를 조회
        List<MainDTO> mainList = textbookService.mainList(subjectId);

        if (mainList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 과목에 대한 정보가 없습니다.");
        }
        // 성공적으로 데이터를 반환
        return ResponseEntity.ok(new ResponseDTO("success", mainList));
    }
}
