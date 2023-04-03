package vn.com.loyalty.core.exception;

import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class OrchestrationException extends RuntimeException{


    private final String message;

    public OrchestrationException(String message) {
        this.message = message;
    }
}