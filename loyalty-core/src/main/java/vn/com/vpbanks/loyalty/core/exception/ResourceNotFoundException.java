package vn.com.vpbanks.loyalty.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResourceNotFoundException extends RuntimeException {
    private Class clazz;
    private String resourceCode;
    private String message;

    public ResourceNotFoundException(Class clazz, String resourceCode) {
        this.clazz = clazz;
        this.resourceCode = resourceCode;
        this.message = "Resource: " + clazz.getName() + " - " +resourceCode+ " not existed!";
    }

}
