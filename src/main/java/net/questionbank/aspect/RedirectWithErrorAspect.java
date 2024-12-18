package net.questionbank.aspect;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.RedirectWithError;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Aspect
@Component
@Log4j2
@RequiredArgsConstructor
public class RedirectWithErrorAspect {

    @Around("@annotation(redirectWithError)")
    public Object redirectWithError(ProceedingJoinPoint proceedingJoinPoint, RedirectWithError redirectWithError) throws Throwable {
        final String REDIRECT_URI = redirectWithError.redirectUri();
        final String ATTRIBUTE_NAME = redirectWithError.attributeName();
        final String ERROR_MESSAGE = redirectWithError.message();
        try{
            return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        }catch(Throwable e){
            log.error(e.getMessage());
            return redirectWithErrorExecute(ERROR_MESSAGE.isEmpty()?e.getMessage():ERROR_MESSAGE,REDIRECT_URI,ATTRIBUTE_NAME,proceedingJoinPoint.getArgs());
        }
    }
    private Object redirectWithErrorExecute(String message, String redirectUrl, String attributeName, Object[] args) {
        log.debug("redirectWithError");
        RedirectAttributes redirectAttributes = null;
        for (Object arg : args) {
            if (arg instanceof RedirectAttributes ra) {
                log.debug("redirectAttribute");
                redirectAttributes = ra;
            }
        }
        if (redirectAttributes != null) {
            log.debug("return redirectAttribute");
            redirectAttributes.addFlashAttribute(attributeName, message);
        }
        return "redirect:" + redirectUrl;
    }
}
