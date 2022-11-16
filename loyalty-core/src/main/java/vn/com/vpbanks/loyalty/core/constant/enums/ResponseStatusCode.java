package vn.com.vpbanks.loyalty.core.constant.enums;

public enum ResponseStatusCode {
    SUCCESS("20000000"), BAD_REQUEST("40000000"), UNAUTHORIZED("40100000"), FORBIDDEN("40300000"), NOT_FOUND("40400000"),
    INTERNAL_SERVER_ERROR("50000000"), BAD_GATEWAY("50200000"), NO_CONTENT("20400000"), BUSINESS_ERROR("40000001"),
    VALIDATION_ERROR("40000002"), INVALID_INPUT_DATA("40000003"), INVALID_DATA_TYPE("40000004"), MISSING_PARAMETER("40000005"),
    MAX_RETRY_ATTEMPTS_REACHED("50000000");

    private final String code;
    private final String message;

    ResponseStatusCode(String code) {
        this.code = code;
        this.message = getMessage();
    }

    public String getMessage() {
        return this.name();
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "ResponseStatus{" + "code='" + code + '\'' + "message='" + message + '\'' + '}';
    }

}