package vn.com.vpbanks.loyalty.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import vn.com.vpbanks.loyalty.core.constant.enums.ResponseStatusCode;

@Getter
public class BaseResponseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final HttpStatus httpStatus;
    private final String code;
    private final String requestId;
    private final transient Object data;
    private final ResponseStatusCode responseStatusCode;

    public BaseResponseException(HttpStatus httpStatus, ResponseStatusCode responseStatusCode, String requestId, Object data) {
        this.httpStatus = httpStatus;
        this.requestId = requestId;
        this.code = responseStatusCode.getCode();
        this.responseStatusCode = responseStatusCode;
        this.data = data;
    }

    public BaseResponseException(ResponseStatusCode responseStatusCode, String requestId, Object data) {
        this.httpStatus = HttpStatus.OK;
        this.requestId = requestId;
        this.code = responseStatusCode.getCode();
        this.data = data;
        this.responseStatusCode = responseStatusCode;
    }

    public BaseResponseException(ResponseStatusCode responseStatusCode, String requestId) {
        this.httpStatus = HttpStatus.OK;
        this.requestId = requestId;
        this.code = responseStatusCode.getCode();
        this.data = null;
        this.responseStatusCode = responseStatusCode;
    }

    public BaseResponseException(ResponseStatusCode responseStatusCode, String code, String requestId, Object data) {
        this.httpStatus = HttpStatus.OK;
        this.requestId = requestId;
        this.code = code;
        this.data = data;
        this.responseStatusCode = responseStatusCode;
    }
}
