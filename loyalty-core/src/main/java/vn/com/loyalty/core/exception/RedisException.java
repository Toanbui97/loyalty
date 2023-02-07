package vn.com.loyalty.core.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisException extends RuntimeException {

    private final String message;

    public RedisException(String message) {
        this.message = message;
    }

}
