package vn.com.vpbanks.loyalty.core.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseRequest<T> {

    String requestId;

    T data;

    public static <T> BaseRequest<T> of(T data){

        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setRequestId(UUID.randomUUID().toString());
        baseRequest.setData(data);
        return baseRequest;
    }

}
