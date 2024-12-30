package net.questionbank.controller;

import jakarta.el.MethodNotFoundException;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import net.questionbank.exception.CustomRuntimeException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.MethodNotAllowedException;

@Logging
@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {
    @Order(1)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    public String handleMethodNotFoundException(HttpRequestMethodNotSupportedException e, Model model) {
        log.info("MethodNotAllowedException : {}",e.getMessage());
        model.addAttribute("errors", "잘못된 접근입니다.");
        return "redirect:/error/error";
    }
    @Order(2)
    @ExceptionHandler(CustomRuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleCustomRuntimeExceptionException(CustomRuntimeException e, Model model) {
        log.info("customRuntimeException : {}",e.getMessage());
        model.addAttribute("errors", e.getMessage());
        return "redirect:/error/error";
    }
    @Order(3)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e, Model model) {
        log.info("Exception : {}",e.getMessage());
        model.addAttribute("errors", "일시적인 오류가 발생했습니다. 다시 시도하세요.");
        return "redirect:/error/error";
    }
}
