package vn.com.loyalty.core.constant.enums;

public enum VoucherStatusCode {

    READY_FOR_BUY("READY_FOR_BY"),
    BOUGHT("BOUGHT"),
    READY_FOR_USER("READY_FOR_USE"),
    USED("USED");

    private String code;

    VoucherStatusCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }
}
