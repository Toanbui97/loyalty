package vn.com.loyalty.core.exception;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PointException extends RuntimeException {

    private final String message;
    private final BigDecimal pointUse;
    private final BigDecimal totalPoint;
    private final String transactionId;
    private final String customerCode;

    public PointException(String transactionId, String customerCode, BigDecimal totalPoint, BigDecimal pointUse) {
        super();
        this.customerCode = customerCode;
        this.transactionId = transactionId;
        this.totalPoint = totalPoint;
        this.pointUse = pointUse;
        this.message = "Not enough point to use. transactionId: " + transactionId + " customerCode: " + customerCode + " totalPoint: " + totalPoint + " pointUse: " + pointUse;
    }
}
