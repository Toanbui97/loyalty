package vn.com.loyalty.core.utils.factory.response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.ResponseStatusCode;

import java.util.List;

@Slf4j
@Component
public class ResponseFactory {

    public <T> ResponseEntity<BodyResponse<T>> success(T data) {
        return ResponseEntity.ok(new BodyResponse<>(0, ResponseStatusCode.SUCCESS, data));
    }
    public <T> ResponseEntity<BodyResponse<T>> success(String uuid, T data) {
        return ResponseEntity.ok(new BodyResponse<>(0, ResponseStatusCode.SUCCESS, uuid, data));
    }
    public <T> ResponseEntity<BodyResponse<T>> success(List<T> dataList) {

        return ResponseEntity.ok(new BodyResponse<>(0, ResponseStatusCode.SUCCESS, dataList));
    }

    public <T> ResponseEntity<BodyResponse<T>> success(String uuid, List<T> dataList) {

        return ResponseEntity.ok(new BodyResponse<>(0, ResponseStatusCode.SUCCESS, uuid, dataList));
    }

    public <T> ResponseEntity<BodyResponse<T>> success(Page<T> page) {

        return ResponseEntity.ok(new BodyResponse<>(0, ResponseStatusCode.SUCCESS, page));
    }

    public ResponseEntity<BodyResponse<Object>> fail(HttpStatus status, ResponseStatusCode statusCode) {
        BodyResponse<Object> generalRes = new BodyResponse<>(statusCode, null, Constants.RESPONSE_STATUS_FAIL);
        log.error(" ====== > Exception - {code: {}, message: {}}", statusCode.getCode(), generalRes.getMessage());
        return ResponseEntity.status(status).body(generalRes);
    }

    public <T> ResponseEntity<BodyResponse<T>> success(String requestId) {
        return ResponseEntity.ok(new BodyResponse<>(ResponseStatusCode.SUCCESS, requestId, Constants.RESPONSE_STATUS_SUCCESS));
    }

    public <T> ResponseEntity<BodyResponse<T>> success(String requestId, T data, ResponseStatusCode statusCode) {
        return ResponseEntity.ok(new BodyResponse<>(statusCode, requestId, Constants.RESPONSE_STATUS_SUCCESS, data));
    }


}
