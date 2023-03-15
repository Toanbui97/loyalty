package vn.com.loyalty.core.constant.enums;

public enum PointStatus {
    ACTIVE("ACTIVE"),
    DEACTIVATE("DEACTIVATE"),
    UNCOUNTED("UNCOUNTED"),
    COUNTED("COUNTED");

    private String code;
    private String name;
    PointStatus(String code) {
        this.code = code;
        this.name= code;
    }
}
