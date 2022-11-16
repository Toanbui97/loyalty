package vn.com.vpbanks.loyalty.core.dto.response.voucher;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.vpbanks.loyalty.core.constant.enums.StatusCode;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherDetailResponse {

    String voucherCode;
    String voucherDetailCode;
    String customerCode;
    StatusCode status;
}
