package vn.com.loyalty.core.constant.enums;

public enum PointStatus {
    ACTIVE("ACTIVE"),
    DEACTIVE("DEACTIVE");

    private String code;
    private String name;
    PointStatus(String code) {
        this.code = code;
        this.name= code;
    }
}
