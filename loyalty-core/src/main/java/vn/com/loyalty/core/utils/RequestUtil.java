package vn.com.loyalty.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import vn.com.loyalty.core.constant.Constants;

import java.util.Base64;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestUtil {
    private final HttpSession httpSession;
    private static final String REQUEST_ID = "requestId";

    public String extractCustomerCodeFromToken(String token)  {

        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            JsonNode node = new ObjectMapper().readTree(payload);
            return node.path("customerCode").asText();
        } catch (Exception e) {
            log.error("Error - e: {}", e);
            return null;
        }
    }

    public String insertValueForPathURI(String uriString, String... values) {
        String uriActual = uriString;
        for (String value : values) {
            uriActual = uriActual.replaceFirst(Constants.REGEX_PATH_URI, value);
        }
        return uriActual;
    }

    @Async
    public String getRequestId() {
        try {
            return (String) httpSession.getAttribute("requestId");
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }

    @Async
    public void setRequestId(String requestId) {
        httpSession.setAttribute(REQUEST_ID, requestId);
    }

    @Async
    public void removeRequestId() {
        httpSession.removeAttribute(REQUEST_ID);
    }
}
