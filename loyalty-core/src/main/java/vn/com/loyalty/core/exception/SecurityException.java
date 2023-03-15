package vn.com.loyalty.core.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityException extends RuntimeException {

    private final String message;

    public SecurityException() {
        this.message = "Permission denied";
    }
}
