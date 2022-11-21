package vn.com.loyalty.core.thirdparty.dto;

import vn.com.loyalty.core.dto.request.BodyRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUpdateRankCustomerRequest extends BodyRequest {

    Long referId;

    String oldRank;

    String newRank;

}
