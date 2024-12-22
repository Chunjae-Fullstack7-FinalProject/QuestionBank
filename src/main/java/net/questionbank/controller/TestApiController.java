package net.questionbank.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.dto.MemberLoginDTO;
import net.questionbank.dto.test.TestDTO;
import net.questionbank.service.step3.Step3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/customExam")
public class TestApiController {
    private final Step3Service step3Service;

    @Value("${file.pdfDir}")
    private String pdfDir;

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

    //pdf 파일 저장
    @PostMapping("/pdf")
    public ResponseEntity<String> savePdf(@RequestBody MultipartFile file) {
        String filename = file.getOriginalFilename();
        try {
            File outputFile = new File(pdfDir + filename);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(file.getBytes());
            }
            log.info("PDF 파일 저장 완료: " + outputFile.getAbsolutePath());
            return ResponseEntity.ok(filename);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).body("PDF 파일 업로드 실패");
        }
    }

    //svg 파일 변환할 때 cors 문제 방지
    @GetMapping("/proxy")
    public ResponseEntity<byte[]> proxySvg(@RequestParam String url) {
        RestTemplate restTemplate = new RestTemplate();
        byte[] svgData = restTemplate.getForObject(url, byte[].class);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/svg+xml"))
                .body(svgData);
    }
}
