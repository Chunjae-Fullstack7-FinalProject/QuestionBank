package net.questionbank.aspect;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Log4j2
@RequiredArgsConstructor
public class LoggingAspect {
    final String doubleLine = "==============================";

    @Around("@annotation(logging)")
    public Object logMethodLifeCycle(ProceedingJoinPoint joinPoint, Logging logging) throws Throwable {
        String method = joinPoint.getSignature().getName(); //메서드명
        String className = joinPoint.getTarget().getClass().getName(); //클래스명
        Object[] args = joinPoint.getArgs(); //메서드 인수

        //메서드 시작시 로그
        log.info(doubleLine);
        log.info(" {}-> {} start", className, method);
        long startTime = System.currentTimeMillis();

        try{
            Object result = joinPoint.proceed(args);
        }catch (Throwable e){
            log.error("error in {}.{}",className,method, e);
            throw e;
        }
        long endTime = System.currentTimeMillis();
        //메서드 종료시 로그
        log.info("{} -> {} end in {} ms", className, method, endTime - startTime);
        return joinPoint.proceed();
    }

    @Around("within(@net.questionbank.annotation.Logging *)")
    public Object logMethodWithin(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodLifeCycle(joinPoint, null);
    }
}
