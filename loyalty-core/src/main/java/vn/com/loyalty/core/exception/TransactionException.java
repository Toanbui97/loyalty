package vn.com.loyalty.core.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionException extends RuntimeException {

    private final String message;

    public TransactionException(String message) {
        this.message = message;
    }
}
