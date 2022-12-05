package vn.com.loyalty.core.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRequest implements Serializable {

    String customerName;
    String customerCode;
    Long activeVoucher;
    BigDecimal totalEpoint;
    BigDecimal totalEloy;
}
