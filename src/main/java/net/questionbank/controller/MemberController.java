package net.questionbank.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import net.questionbank.annotation.RedirectWithError;
import net.questionbank.domain.Member;
import net.questionbank.dto.MemberLoginDTO;
import net.questionbank.dto.MemberRegisterDTO;
import net.questionbank.exception.CustomRuntimeException;
import net.questionbank.service.MemberServiceIf;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@Logging
@RedirectWithError
@Log4j2
public class MemberController {
    private final MemberServiceIf memberService;
    @GetMapping("/login")
    public String loginGet() {
        return "member/login";
    }

    @RedirectWithError(redirectUri = "/member/login")
    @PostMapping("/login")
    public String loginPost(@Valid MemberLoginDTO memberLoginDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) {
        if(bindingResult.hasErrors()) {
            throw new CustomRuntimeException(getBindingResultErrorMessage(bindingResult));
        }
        try {
            MemberLoginDTO loginDTO = memberService.login(memberLoginDTO);
            session.setAttribute("loginDTO", loginDTO);
        }catch(CustomRuntimeException e){
            throw e;
        }
        catch(Exception e) {
            throw new CustomRuntimeException("오류가 발생했습니다. 다시시도해주세요.");
        }
        return "redirect:/main";
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