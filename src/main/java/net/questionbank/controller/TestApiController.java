package net.questionbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

    @PostMapping("/step3/save")
    public ResponseEntity<?> saveTest(TestDTO testDTO, List<Long> itemIdList, HttpServletRequest request) {
        try {
            step3Service.saveTestInfo(testDTO, itemIdList);
            return ResponseEntity.ok("save");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
