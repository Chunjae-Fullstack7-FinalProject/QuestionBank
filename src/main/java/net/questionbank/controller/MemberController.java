package net.questionbank.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import net.questionbank.annotation.RedirectWithError;
import net.questionbank.dto.MemberLoginDTO;
import net.questionbank.dto.MemberRegisterDTO;
import net.questionbank.exception.CustomRuntimeException;
import net.questionbank.service.MemberServiceIf;
import net.questionbank.util.ApiResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@Logging
@Log4j2
public class MemberController {
    private final MemberServiceIf memberService;
    @RedirectWithError(redirectUri = "/error/error")
    @GetMapping("/login")
    public String loginGet(HttpSession session, RedirectAttributes redirectAttributes) {
        return "member/login";
    }

    @RedirectWithError(redirectUri = "/member/login")
    @PostMapping("/login")
    public String loginPost(@Valid MemberLoginDTO memberLoginDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest req, HttpSession session) {
        if(bindingResult.hasErrors()) {
            throw new CustomRuntimeException(getBindingResultErrorMessage(bindingResult));
        }
        try {
            MemberLoginDTO loginDTO = memberService.login(memberLoginDTO);
            session.setAttribute("loginDto", loginDTO);
            Boolean goCustomExam = (Boolean)session.getAttribute("goCustomExam");
            if(goCustomExam!=null && goCustomExam) {
                log.info("goCustomExam234234");
                session.removeAttribute("goCustomExam");
                req.setAttribute("subjectId", session.getAttribute("subjectId"));
                session.removeAttribute("subjectId");
                return "forward:/customExam/step0";
            }
        }catch(CustomRuntimeException e){
            throw e;
        }
        catch(Exception e) {
            throw new CustomRuntimeException("오류가 발생했습니다. 다시시도해주세요.");
        }
        return "redirect:/main";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loginDto");
        return "redirect:/main";
    }

    @GetMapping("/checkId/{member-id}")
    public ResponseEntity<ApiResponseUtil<?>> checkId(@PathVariable(value = "member-id") String memberId){
        try{
            Boolean result = memberService.exists(memberId);
            return ResponseEntity.ok(ApiResponseUtil.success("아이디 중복조회", result));
        }catch(Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponseUtil.error(e.getMessage()));
        }
    }

    @GetMapping("/register")
    public String registerGet() {
        return "member/register";
    }

    @RedirectWithError(redirectUri = "/member/register")
    @PostMapping("/register")
    public String registerPost(@Valid MemberRegisterDTO memberRegisterDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()) {
            throw new CustomRuntimeException(getBindingResultErrorMessage(bindingResult));
        }
        try{
            memberService.register(memberRegisterDTO);
        }catch(CustomRuntimeException e){
            redirectAttributes.addFlashAttribute("memberRegisterDTO", memberRegisterDTO);
            throw e;
        }
        catch(Exception e) {
            redirectAttributes.addFlashAttribute("memberRegisterDTO", memberRegisterDTO);
            throw new CustomRuntimeException("오류가 발생했습니다. 다시시도해주세요.");
        }
        return "redirect:/member/login";
    }

    private String getBindingResultErrorMessage(BindingResult bindingResult) {
        StringBuilder errorMessage = new StringBuilder();
        bindingResult.getAllErrors().forEach(err->{errorMessage.append(err.getDefaultMessage()).append("\n");});
        return errorMessage.toString();
    }
}