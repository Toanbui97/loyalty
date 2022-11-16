package vn.com.vpbanks.loyalty.core.dto.response.voucher;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherResponse {

    String voucherName;
    String description;
    Long totalVoucher;
    Long active;
    Long inActive;
}
