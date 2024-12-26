package net.questionbank.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.questionbank.domain.Report;
import net.questionbank.dto.MemberLoginDTO;
import net.questionbank.dto.report.ReportRegisterDTO;
import net.questionbank.service.report.ReportServiceIf;
import net.questionbank.util.ApiResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportRestController {
    private final ReportServiceIf reportService;
    @PostMapping
    public ResponseEntity<ApiResponseUtil<?>> register(@Valid @ModelAttribute ReportRegisterDTO registerDTO, BindingResult bindingResult, HttpSession session) {
        try{
            if(bindingResult.hasErrors()) {
                StringBuilder errors = new StringBuilder();
                bindingResult.getAllErrors().forEach(err->{errors.append(err.getDefaultMessage()).append("\n");});
                return ResponseEntity.badRequest().body(ApiResponseUtil.error(errors.toString()));
            }
            //MemberLoginDTO loginDTO = (MemberLoginDTO)session.getAttribute("loginDTO");

            registerDTO.setMemberId("member1");
            Report report = reportService.register(registerDTO);
            return ResponseEntity.ok(ApiResponseUtil.success("등록 성공", report));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResponseUtil.error(e.getMessage()));
        }
    }

}
