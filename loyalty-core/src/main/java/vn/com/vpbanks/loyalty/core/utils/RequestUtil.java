package vn.com.vpbanks.loyalty.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import vn.com.vpbanks.loyalty.core.constant.Constants;
import vn.com.vpbanks.loyalty.core.dto.request.BaseRequest;

import java.util.Base64;

@UtilityClass
@Slf4j
public class RequestUtil {

    public String extractCustomerCodeFromToken(String token)  {

        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            JSONObject jsonObject = new JSONObject(payload);
            return jsonObject.getString("customerCode");
        } catch (Exception e) {
            log.error("Error - e: {}", e);
            return null;
        }
    }

    public String insertValueForPathURI(String uriString, String... values) {
        String uriActual = uriString;
        for (String value : values) {
            uriActual.replaceFirst(Constants.REGEX_PATH_URI, value);
        }
        return uriActual;
    }
}
