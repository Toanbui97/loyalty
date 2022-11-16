package vn.com.vpbanks.loyalty.core.constant.enums;

public enum StatusCode {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private String code;

    StatusCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }
}
