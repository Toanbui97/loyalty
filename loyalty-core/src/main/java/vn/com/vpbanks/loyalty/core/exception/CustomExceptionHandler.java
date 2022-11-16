package vn.com.vpbanks.loyalty.core.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vn.com.vpbanks.loyalty.core.constant.Constants;
import vn.com.vpbanks.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.vpbanks.loyalty.core.utils.factory.response.ResponseFactory;

import java.util.*;

import static vn.com.vpbanks.loyalty.core.constant.enums.ResponseStatusCode.*;


@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler {

    private final ResponseFactory responseFactory;

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
        log.error("Error - ex: {}", ex);
        List<String> details = Collections.singletonList(ex.getLocalizedMessage());
        BodyResponse<Object> response = new BodyResponse<>(INTERNAL_SERVER_ERROR, null, Constants.RESPONSE_STATUS_FAIL, details);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(BaseResponseException.class)
    public final ResponseEntity<BodyResponse<Object>> handleBaseResponseException(BaseResponseException ex) {
        log.error(" ====== > Exception root cause: {}", Arrays.stream(ex.getStackTrace()).toArray());
        return responseFactory.fail(ex.getHttpStatus(), ex.getResponseStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<BodyResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error("Error - ex: {}", ex);
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(String.format(Objects.requireNonNull(error.getDefaultMessage()), ((FieldError) error).getField()));
        }
        BodyResponse<Object> response = new BodyResponse<>(INVALID_INPUT_DATA, null, Constants.RESPONSE_STATUS_FAIL, details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<BodyResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Error - ex: {}", ex);
        List<String> details = new ArrayList<>();
        BodyResponse<Object> response = new BodyResponse<>(INVALID_INPUT_DATA, null, Constants.RESPONSE_STATUS_FAIL, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
