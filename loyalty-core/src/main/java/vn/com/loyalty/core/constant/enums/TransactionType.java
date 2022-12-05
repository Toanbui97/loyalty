package vn.com.loyalty.core.constant.enums;

public enum TransactionType {
    STOCK_TYPE("STOCK_TYPE"),
    BOUND_TYPE("BOUND_TYPE");

    private String type;

    TransactionType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

}
