package vn.com.loyalty.core.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.utils.ObjectUtil;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;

import java.util.UUID;

@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApiLogging {

    private final HttpSession httpSession;
    private final HttpServletRequest httpRequest;
    private static final String REQUEST_ID = "requestId";

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restApiPointCut() {}

    @Before("restApiPointCut()")
    public void beforeCallApi(JoinPoint joinPoint) {

        if (joinPoint.getArgs()[0] instanceof BodyRequest<?> request) {
            if (!StringUtils.hasText(request.getRequestId())) {
                String reqId =  UUID.randomUUID().toString();
                httpSession.setAttribute(REQUEST_ID, reqId);
                request.setRequestId(reqId);
            } else {
                httpSession.setAttribute(REQUEST_ID, request.getRequestId());
            }
            log.info("""
                    
                    ===================> API Request: {}.{}
                    {}: {}
                    {}
                    """
                    ,joinPoint.getSignature().getDeclaringType().getName(), joinPoint.getSignature().getName()
                    ,httpRequest.getMethod(), httpRequest.getRequestURL(), ObjectUtil.prettyPrintJsonObject(request));
        } else  {
            log.info("""
                    
                    ===================> API Request: {}.{}
                    {}: {}
                    {}
                    """
                    ,joinPoint.getSignature().getDeclaringType().getName(), joinPoint.getSignature().getName()
                    ,httpRequest.getMethod(), httpRequest.getRequestURL(), ObjectUtil.prettyPrintJsonObject(joinPoint.getArgs()[0]));
        }
    }

    @AfterReturning(value = "restApiPointCut()", returning = "response")
    public void afterReturnResponse(JoinPoint joinPoint, ResponseEntity<?> response) {
        if (response != null && response.getBody() instanceof BodyResponse<?> body) {
            body.setRequestId((String) httpSession.getAttribute(REQUEST_ID));
            log.info("""
                    
                    ===================> API Response: {}.{}
                    {}: {}
                    {}
                    """
                    , joinPoint.getSignature().getDeclaringType().getName(), joinPoint.getSignature().getName()
                    ,httpRequest.getMethod(), httpRequest.getRequestURL(), ObjectUtil.prettyPrintJsonObject(response.getBody()));
        }
    }
}
