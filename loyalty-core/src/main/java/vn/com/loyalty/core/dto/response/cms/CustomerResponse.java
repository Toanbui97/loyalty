package vn.com.loyalty.core.dto.response.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerResponse {

    String customerName;
    String customerCode;
    BigDecimal epoint;
    BigDecimal rpoint;
    String rankCode;
    LocalDate rankExpired;
    Long activeVoucher;
}
