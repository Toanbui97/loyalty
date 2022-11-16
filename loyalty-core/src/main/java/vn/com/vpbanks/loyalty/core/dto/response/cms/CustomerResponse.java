package vn.com.vpbanks.loyalty.core.dto.response.cms;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerResponse {

    String customerName;
    String customerCode;
    Long activeVouchers;
}
