package vn.com.vpbanks.loyalty.core.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseRequest<T> implements Serializable {

    String requestId;

    T data;

    public static <T> BaseRequest<T> of(T data){

        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setRequestId(UUID.randomUUID().toString());
        baseRequest.setData(data);
        return baseRequest;
    }

}
