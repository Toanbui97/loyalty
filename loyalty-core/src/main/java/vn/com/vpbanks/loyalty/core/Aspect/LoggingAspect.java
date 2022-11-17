package vn.com.vpbanks.loyalty.core.Aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import vn.com.vpbanks.loyalty.core.dto.request.BodyRequest;
import vn.com.vpbanks.loyalty.core.utils.factory.response.BodyResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class LoggingAspect {

    private final HttpSession httpSession;
    private final HttpServletRequest httpRequest;
    private static final String REQUEST_ID = "requestId";

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restApiPointCut() {}

    @Pointcut("within(vn.com.vpbanks.loyalty.core.thirdparty.service.WebClientService+)")
    public void webClientPointCut() {}

    @Before("restApiPointCut()")
    public void beforeCallApi(JoinPoint joinPoint) throws JsonProcessingException {
        log.info("\n===================> Request: {}", httpRequest.getRequestURI());
        if (joinPoint.getArgs()[0] instanceof BodyRequest) {
            log.info("{}.{} \n{}", joinPoint.getSignature().getDeclaringType().getName()
                , joinPoint.getSignature().getName(), this.prettyPrintJsonObject(joinPoint.getArgs()[0]));
            if (!StringUtils.hasText(((BodyRequest<?>) joinPoint.getArgs()[0]).getRequestId())) {
                httpSession.setAttribute(REQUEST_ID, UUID.randomUUID().toString());
            } else {
                httpSession.setAttribute(REQUEST_ID, ((BodyRequest<?>) joinPoint.getArgs()[0]).getRequestId());
            }
        }
    }

    @AfterReturning(value = "restApiPointCut()", returning = "response")
    public void afterReturnResponse(JoinPoint joinPoint, ResponseEntity<?> response) throws JsonProcessingException {
        if (response != null) {
            log.info("\n===================> Response: \n{}.{} \n {}", joinPoint.getSignature().getDeclaringType().getName()
                    , joinPoint.getSignature().getName(), this.prettyPrintJsonObject(response.getBody()));
            BodyResponse<?> bodyResponse = (BodyResponse<?>) response.getBody();
            if (bodyResponse != null) {
                bodyResponse.setRequestId((String) httpSession.getAttribute(REQUEST_ID));
            }
        }
    }

    @Before("webClientPointCut()")
    public void beforeWebClientCall(JoinPoint joinPoint) throws JsonProcessingException {
        log.info("\n===================> Web client call: \n{}.{} \n{}", joinPoint.getSignature().getDeclaringType().getName()
                , joinPoint.getSignature().getName(), this.prettyPrintJsonObject(joinPoint.getArgs()[0]));
    }

    @AfterReturning(value = "webClientPointCut()", returning = "response")
    public void afterWebClientCall(JoinPoint joinPoint, Object response) throws JsonProcessingException {
        if (response != null) {
            log.info("\n===================> Web client response: \n{}.{} \n {}", joinPoint.getSignature().getDeclaringType().getName()
                    , joinPoint.getSignature().getName(), this.prettyPrintJsonObject(response));
        }
    }


    private String prettyPrintJsonObject(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }


}
