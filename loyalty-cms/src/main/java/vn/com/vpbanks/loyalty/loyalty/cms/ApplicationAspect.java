package vn.com.vpbanks.loyalty.loyalty.cms;

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
public class ApplicationAspect {


}
