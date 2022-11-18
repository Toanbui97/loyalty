package vn.com.vpbanks.loyalty.core.utils;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import vn.com.vpbanks.loyalty.core.constant.Constants;

import java.util.Base64;

@UtilityClass
@Slf4j
public class RequestUtil {

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
}
