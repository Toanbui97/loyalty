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
public class CustomerMessage implements Serializable {
    String customerCode;
    String transactionId;
    Long activeVoucher;
    BigDecimal epointGain;
    BigDecimal epointSpend;
    BigDecimal rpointGain;
}
