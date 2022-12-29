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
public class CustomerMessageDto implements Serializable {
    String customerName;
    String customerCode;
    Long activeVoucher;
    BigDecimal epointGained;
    BigDecimal eloyGained;
}
