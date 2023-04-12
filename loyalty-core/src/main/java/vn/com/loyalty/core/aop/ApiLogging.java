package vn.com.loyalty.core.aop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.utils.ObjectUtil;
import vn.com.loyalty.core.utils.RequestUtil;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;

import java.util.UUID;

@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApiLogging {

    private final HttpSession httpSession;
    private final HttpServletRequest httpRequest;
    private final RequestUtil requestUtil;

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restApiPointCut() {
    }

    @Before("restApiPointCut()")
    public void beforeCallApi(JoinPoint joinPoint) {
        var requestBody = getRequestBody(joinPoint);

        if (requestBody != null) {
            if (!StringUtils.hasText(requestBody.getRequestId())) {
                requestBody.setRequestId(UUID.randomUUID().toString());
            }

            requestUtil.setRequestId(requestBody.getRequestId());
            log.info("""

                            ===================> API Request: {}.{}
                            {}: {}
                            {}
                            """
                    , joinPoint.getSignature().getDeclaringType().getName(), joinPoint.getSignature().getName()
                    , httpRequest.getMethod(), httpRequest.getRequestURL(), ObjectUtil.prettyPrintJsonObject(requestBody));
        } else {
            log.info("""

                            ===================> API Request: {}.{}
                            {}: {}
                            {}
                            """
                    , joinPoint.getSignature().getDeclaringType().getName(), joinPoint.getSignature().getName()
                    , httpRequest.getMethod(), httpRequest.getRequestURL(), null);
        }
    }


    public BodyRequest<?> getRequestBody(JoinPoint joinPoint) {
        var args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return null;
        }

        var length = args.length;
        int index;
        BodyRequest<?> request = null;
        for (index = 0; index < length; index++) {
            if (args[index] instanceof BodyRequest<?> bodyRequest) {
                request = bodyRequest;
                break;
            }
        }
        return request;
    }

    @AfterReturning(value = "restApiPointCut()", returning = "response")
    public void afterReturnResponse(JoinPoint joinPoint, ResponseEntity<?> response) {
        if (response != null && response.getBody() instanceof BodyResponse<?> body) {
            body.setRequestId(requestUtil.getRequestId());
            requestUtil.removeRequestId();
            log.info("""

                            ===================> API Response: {}.{}
                            {}: {}
                            {}
                            """
                    , joinPoint.getSignature().getDeclaringType().getName(), joinPoint.getSignature().getName()
                    , httpRequest.getMethod(), httpRequest.getRequestURL(), ObjectUtil.prettyPrintJsonObject(response.getBody()));
        }
    }
}
