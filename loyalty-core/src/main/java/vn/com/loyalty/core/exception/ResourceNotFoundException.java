package vn.com.loyalty.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResourceNotFoundException extends RuntimeException {
    private final Class<?> clazz;
    private final String resourceCode;
    private final String message;

    public <T> ResourceNotFoundException(Class<T> clazz, String resourceCode) {
        this.clazz = clazz;
        this.resourceCode = resourceCode;
        this.message = "Resource: " + clazz.getName() + " - " +resourceCode+ " not existed!";
    }

}
