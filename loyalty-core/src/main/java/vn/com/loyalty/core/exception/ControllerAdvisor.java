package vn.com.loyalty.core.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.core.utils.factory.response.ResponseFactory;

import java.util.*;

import static vn.com.loyalty.core.constant.enums.ResponseStatusCode.*;


@ControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvisor {

    private final ResponseFactory responseFactory;

    @ExceptionHandler(SecurityException.class)
    public final ResponseEntity<BodyResponse<Object>> handleSecurityException(SecurityException exception) {
        BodyResponse<Object> response = new BodyResponse<>(UNAUTHORIZED, null, Constants.RESPONSE_STATUS_FAIL, exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<BodyResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        BodyResponse<Object> response = new BodyResponse<>(INTERNAL_SERVER_ERROR, null, Constants.RESPONSE_STATUS_FAIL, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ResourceExistedException.class)
    public final ResponseEntity<BodyResponse<Object>> handleResourceExistedException(ResourceExistedException ex) {
        BodyResponse<Object> response = new BodyResponse<>(INTERNAL_SERVER_ERROR, null, Constants.RESPONSE_STATUS_FAIL, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BaseResponseException.class)
    public final ResponseEntity<BodyResponse<Object>> handleBaseResponseException(BaseResponseException ex) {
        return responseFactory.fail(ex.getHttpStatus(), ex.getResponseStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<BodyResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(String.format(Objects.requireNonNull(error.getDefaultMessage()), ((FieldError) error).getField()));
        }
        BodyResponse<Object> response = new BodyResponse<>(INVALID_INPUT_DATA, null, Constants.RESPONSE_STATUS_FAIL, details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
        List<String> details = Collections.singletonList(ex.getLocalizedMessage());
        BodyResponse<Object> response = new BodyResponse<>(INTERNAL_SERVER_ERROR, null, Constants.RESPONSE_STATUS_FAIL, details);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
