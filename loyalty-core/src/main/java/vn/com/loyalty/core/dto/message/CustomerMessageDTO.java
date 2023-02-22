package vn.com.loyalty.core.dto.message;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMessageDTO implements Serializable {
    String customerCode;
    Long activeVoucher;
    BigDecimal epoint;
    BigDecimal rpoint;
}
