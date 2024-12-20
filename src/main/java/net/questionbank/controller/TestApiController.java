package net.questionbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.dto.MemberLoginDTO;
import net.questionbank.dto.test.TestDTO;
import net.questionbank.service.step3.Step3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/customExam")
public class TestApiController {
    private final Step3Service step3Service;

    @PostMapping("/save")
    public ResponseEntity<String> saveTest(@RequestBody TestDTO testDTO, HttpSession session) {
//        log.info("saveTest: {}", testDTO);
        try {
            if(testDTO.getTitle().length()>20) {
                throw new Exception("시험지명은 20자 이하로 입력해주세요.");
            }
            MemberLoginDTO loginDto = (MemberLoginDTO) session.getAttribute("loginDto");

            if(loginDto == null) {
                loginDto = MemberLoginDTO.builder().memberId("test1").name("이름").build();
            }
            testDTO.setUserId(loginDto.getMemberId());
            testDTO.setUserName(loginDto.getName());
            step3Service.saveTestInfo(testDTO, testDTO.getItemIdList());

            return ResponseEntity.ok("save");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
