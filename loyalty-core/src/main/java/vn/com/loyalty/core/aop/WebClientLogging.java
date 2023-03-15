package vn.com.loyalty.core.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class WebClientLogging {

    private final ObjectMapper objectMapper;

    @Pointcut(value = "execution(* vn.com.loyalty.core.service.internal.impl.WebClientCommonServiceImpl.setUpUriAndBodyAndHeaders(String, String, Object , Object , Object)) && args(baseUrl, uri,  params, requestBody,  method)", argNames = "baseUrl,uri,params,requestBody,method")
    public void webclientSetupLPointCut(String baseUrl, String uri, MultiValueMap<String, String> params, Object requestBody, HttpMethod method){};

    @After(value = "webclientSetupLPointCut(baseUrl, uri,  params, requestBody,  method)", argNames = "baseUrl,uri,params,requestBody,method")
    public void webclientSetupLogging(String baseUrl, String uri, MultiValueMap<String, String> params, Object requestBody, HttpMethod method) throws JsonProcessingException {
        log.info("""
                 
                ===================> Web Client Request:
                URL: {}: {}{}
                {}
                """
                , method, baseUrl, uri, requestBody != null ? objectMapper.writeValueAsString(requestBody) : "");
    }

    @Pointcut(value = "execution(* vn.com.loyalty.core.service.internal.WebClientCommonService.*(*))")
    public void webclientResponsePointCut(){};

    @AfterReturning(value = "webclientResponsePointCut()", returning = "response")
    public void webclientResponseLogging(JoinPoint joinPoint, Object response) throws JsonProcessingException {
        log.info("""
                
                ===================> Web Client Response: 
                {}
                """
        , objectMapper.writeValueAsString(response));
    }
}
