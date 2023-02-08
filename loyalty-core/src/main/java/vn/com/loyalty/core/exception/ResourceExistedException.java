package vn.com.loyalty.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResourceExistedException extends RuntimeException{
    private final Class clazz;
    private final String resourceCode;
    private final String message;

    public ResourceExistedException(Class clazz, String resourceCode) {
        this.clazz = clazz;
        this.resourceCode = resourceCode;
        this.message = "Resource: " + clazz.getName() + " - " +resourceCode+ " existed!";
    }
}
