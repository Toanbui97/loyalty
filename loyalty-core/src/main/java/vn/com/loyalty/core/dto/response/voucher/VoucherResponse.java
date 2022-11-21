package vn.com.loyalty.core.dto.response.voucher;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoucherResponse {

    String voucherName;
    String description;
    Long totalVoucher;
    Long active;
    Long inActive;
}
