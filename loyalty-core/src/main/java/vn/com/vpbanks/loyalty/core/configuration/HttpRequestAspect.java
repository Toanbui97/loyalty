package vn.com.vpbanks.loyalty.core.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import vn.com.vpbanks.loyalty.core.dto.request.BaseRequest;
import vn.com.vpbanks.loyalty.core.utils.factory.response.BodyResponse;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class HttpRequestAspect {

    private final HttpSession httpSession;

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restApiPointCut(){};

    @Before("restApiPointCut()")
    public void settingPropertiesRequest(JoinPoint joinPoint) throws JsonProcessingException {
        log.info("===================> Resquest to {}", joinPoint.getSignature());
        log.info("===================> Request: \n {}", this.prettyPrintJsonObject(joinPoint.getArgs()[0]));
        if (joinPoint.getArgs()[0] instanceof BaseRequest) {
            if (!StringUtils.hasText(((BaseRequest<?>) joinPoint.getArgs()[0]).getRequestId())) {
                httpSession.setAttribute("requestId", UUID.randomUUID().toString());
            } else {
                httpSession.setAttribute("requestId", ((BaseRequest<?>) joinPoint.getArgs()[0]).getRequestId());
            }
        }
    }

    @AfterReturning(value = "restApiPointCut()", returning = "response")
    public void settingPropertiesResponse(Object response) throws JsonProcessingException {
        log.info("===================> Response: \n {}", this.prettyPrintJsonObject(response));
        if (response instanceof ResponseEntity) {
            BodyResponse bodyResponse = (BodyResponse) ((ResponseEntity<?>) response).getBody();
            bodyResponse.setRequestId((String) httpSession.getAttribute("requestId"));
        }
    }

    private String prettyPrintJsonObject(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}
