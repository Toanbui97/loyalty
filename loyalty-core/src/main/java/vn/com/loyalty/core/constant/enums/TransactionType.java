package vn.com.loyalty.core.constant.enums;

public enum TransactionType {
    STOCK_TYPE("STOCK"),
    BOUND_TYPE("BOUND");

    private String type;

    TransactionType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
