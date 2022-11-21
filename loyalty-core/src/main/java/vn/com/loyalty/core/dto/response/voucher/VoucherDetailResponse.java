package vn.com.loyalty.core.dto.response.voucher;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.constant.enums.VoucherStatusCode;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoucherDetailResponse {

    String voucherCode;
    String voucherDetailCode;
    String customerCode;
    VoucherStatusCode status;
}
