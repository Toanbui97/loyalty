package com.vpbanks.loyalty.voucher.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Slf4j
public class AspectService {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void apiPointCut(){}


    @Before("apiPointCut()")
    public void logRequestComeIn(JoinPoint joinPoint){
        log.info(joinPoint.getTarget().getClass().toGenericString());
    }

}
