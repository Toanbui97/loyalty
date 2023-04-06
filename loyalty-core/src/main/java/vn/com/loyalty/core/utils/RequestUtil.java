package vn.com.loyalty.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import vn.com.loyalty.core.constant.Constants;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RequestUtil {
    private static final String REQUEST_ID = "requestId";
    private final Map<String, String> attributes;

    public RequestUtil() {
        this.attributes = new ConcurrentHashMap<>();
    }

    public String extractCustomerCodeFromToken(String token)  {

        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            JsonNode node = new ObjectMapper().readTree(payload);
            return node.path("customerCode").asText();
        } catch (Exception e) {
            log.error("Error: ", e);
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

    public String getRequestId() {
        if (StringUtils.hasText(this.attributes.get(REQUEST_ID))) return attributes.get(REQUEST_ID);
        return UUID.randomUUID().toString();
    }

    public void setRequestId(String requestId) {
        this.attributes.put(REQUEST_ID, requestId);
    }

    public void removeRequestId() {
        this.attributes.remove(REQUEST_ID);
    }
}
