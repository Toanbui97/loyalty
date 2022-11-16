package vn.com.vpbanks.loyalty.core.thirdparty.cms.dto;

import vn.com.vpbanks.loyalty.core.dto.request.BaseRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUpdateRankCustomerRequest extends BaseRequest {

    Long referId;

    String oldRank;

    String newRank;

}
