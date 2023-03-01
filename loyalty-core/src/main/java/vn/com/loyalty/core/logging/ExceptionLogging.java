package vn.com.loyalty.core.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionLogging {

    @Pointcut("within(vn.com.loyalty..*) && execution(* *(..))")
    public void exceptionPointCut(){}

    @AfterThrowing(value = "exceptionPointCut()", throwing = "exception")
    public void exceptionLog(JoinPoint joinPoint, Throwable exception) {
        log.error("Exception: {}.{}"
        , joinPoint.getSignature().getDeclaringType().getName(), joinPoint.getSignature().getName()
        , exception);
    }
}
