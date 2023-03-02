package vn.com.loyalty.core.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BodyRequest<T> implements Serializable {

    String requestId;
    T data;

    public static <T> BodyRequest<T> of(T data){

        BodyRequest<T> bodyRequest = new BodyRequest<>();
        bodyRequest.setRequestId(UUID.randomUUID().toString());
        bodyRequest.setData(data);
        return bodyRequest;
    }

}
