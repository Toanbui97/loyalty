package vn.com.vpbanks.loyalty.core.utils.factory.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.com.vpbanks.loyalty.core.constant.Constants;
import vn.com.vpbanks.loyalty.core.constant.enums.ResponseStatusCode;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ResponseFactory {

    public <T> ResponseEntity<BodyResponse<T>> success(T data) {

        return ResponseEntity.ok(new BodyResponse<>(ResponseStatusCode.SUCCESS, UUID.randomUUID().toString(), Constants.RESPONSE_STATUS_SUCCESS));
    }
    public <T> ResponseEntity<BodyResponse<T>> success(List<T> dataList) {

        return ResponseEntity.ok(new BodyResponse<>(ResponseStatusCode.SUCCESS, UUID.randomUUID().toString(), Constants.RESPONSE_STATUS_SUCCESS));
    }

    public <T> ResponseEntity<BodyResponse<T>> success(String requestId) {
        return ResponseEntity.ok(new BodyResponse<>(ResponseStatusCode.SUCCESS, requestId, Constants.RESPONSE_STATUS_SUCCESS));
    }

    public <T> ResponseEntity<BodyResponse<T>> success(String requestId, T data) {
        return ResponseEntity.ok(new BodyResponse<>(ResponseStatusCode.SUCCESS, requestId, Constants.RESPONSE_STATUS_SUCCESS, data));
    }

    public <T> ResponseEntity<BodyResponse<T>> success(String requestId, T data, ResponseStatusCode statusCode) {
        return ResponseEntity.ok(new BodyResponse<>(statusCode, requestId, Constants.RESPONSE_STATUS_SUCCESS, data));
    }

    public ResponseEntity<BodyResponse<Object>> fail(HttpStatus status, ResponseStatusCode statusCode) {

        BodyResponse<Object> generalRes = new BodyResponse<>(statusCode, null, Constants.RESPONSE_STATUS_FAIL);
        log.error(" ====== > Exception - {code: {}, message: {}}", statusCode.getCode(), generalRes.getMessage());
        return ResponseEntity.status(status).body(generalRes);
    }
}
