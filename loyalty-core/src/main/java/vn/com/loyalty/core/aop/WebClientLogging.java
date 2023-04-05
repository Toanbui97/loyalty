package vn.com.loyalty.core.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientLogging {

    private final ObjectMapper objectMapper;

    @Pointcut("execution(* vn.com.loyalty.core.service.internal.WebClientService.*(..))")
    public void webclientResponsePointCut(){}

    @SneakyThrows
    @AfterReturning(value = "webclientResponsePointCut()", returning = "response")
    public void webclientResponseLogging(JoinPoint joinPoint, Object response) {
        log.info("""
                
                ===================> Web Client Response: 
                {}
                """
        , objectMapper.writeValueAsString(response));
    }
}
