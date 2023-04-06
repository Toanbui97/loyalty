package vn.com.loyalty.core.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import vn.com.loyalty.core.utils.DateTimeUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherRequest implements Serializable {
    String customerCode;
    String voucherName;
    String description;
    String voucherCode;
    Long totalVoucher;
    BigDecimal discountPercent;
    @JsonFormat(pattern = DateTimeUtils.FORMAT_DD_MM_YYYY)
    LocalDate expireTime;
    BigDecimal price;
}
