package vn.com.vpbanks.loyalty.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.com.vpbanks.loyalty.core.entity.VoucherEntity;

@Getter
@Setter
@AllArgsConstructor
public class ResourceNotFoundException extends RuntimeException {
    private String resource;
    private String resourceCode;
    private String message;

    public ResourceNotFoundException(String resource, String resourceCode) {
    }

    public void setMessage(String message) {

        this.message = "Resource: " + resource + " - " + "not existed!";
    }
}
