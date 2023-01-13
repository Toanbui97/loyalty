package vn.com.loyalty.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionException extends RuntimeException {

    private String message;

    public TransactionException(String message) {
        this.message = message;
    }
}
