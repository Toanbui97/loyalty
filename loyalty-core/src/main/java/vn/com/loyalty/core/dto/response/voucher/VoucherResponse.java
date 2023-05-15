package vn.com.loyalty.core.dto.response.voucher;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoucherResponse {

    String voucherName;
    String voucherCode;
    String description;
    Long totalVoucher;
    Long active;
    Long inActive;
    BigDecimal discountPercent;
    LocalDate expireTime;
    BigDecimal price;
    String rankRequire;
    List<VoucherDetailResponse> detailEntities;
}
