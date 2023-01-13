package vn.com.loyalty.core.dto.response.cms;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

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
    BigDecimal totalEpoint;
    BigDecimal totalRpoint;
    Long activeVoucher;
}
