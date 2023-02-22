package vn.com.loyalty.core.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        if (joinPoint.getArgs()[0] instanceof BodyRequest) {
            log.info("===================> Request: \n{}.{}\n{}: {}\n{}",joinPoint.getSignature().getDeclaringType().getName(), joinPoint.getSignature().getName()
                    ,httpRequest.getMethod(), httpRequest.getRequestURL(), ObjectUtil.prettyPrintJsonObject(joinPoint.getArgs()[0]));
            if (!StringUtils.hasText(((BodyRequest<?>) joinPoint.getArgs()[0]).getRequestId())) {
                httpSession.setAttribute(REQUEST_ID, UUID.randomUUID().toString());
            } else {
                httpSession.setAttribute(REQUEST_ID, ((BodyRequest<?>) joinPoint.getArgs()[0]).getRequestId());
            }
        }
    }

    @AfterReturning(value = "restApiPointCut()", returning = "response")
    public void afterReturnResponse(JoinPoint joinPoint, ResponseEntity<?> response) {
        if (response != null) {
            ((BodyResponse) response.getBody()).setRequestId((String) httpSession.getAttribute(REQUEST_ID));
            log.info("\n===================> Response: \n{}.{} \n {}", joinPoint.getSignature().getDeclaringType().getName()
                    , joinPoint.getSignature().getName(), ObjectUtil.prettyPrintJsonObject(response.getBody()));
        }
    }

}
