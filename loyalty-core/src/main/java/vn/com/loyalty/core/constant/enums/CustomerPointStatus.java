package vn.com.loyalty.core.constant.enums;

public enum CustomerPointStatus {
    ACTIVE("ACTIVE"),
    DEACTIVE("DEACTIVE");

    private String code;
    private String name;
    CustomerPointStatus(String code) {
        this.code = code;
        this.name= code;
    }
}
