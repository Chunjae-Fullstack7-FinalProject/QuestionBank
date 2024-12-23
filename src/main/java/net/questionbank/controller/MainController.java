package net.questionbank.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.dto.main.MainDTO;
import net.questionbank.dto.main.ResponseDTO;
import net.questionbank.dto.main.SubjectRequestDTO;
import net.questionbank.service.textbook.TextbookServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/")
public class MainController {
    private final TextbookServiceImpl textbookService;

    @GetMapping("/main")
    public String mainPage(){
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
