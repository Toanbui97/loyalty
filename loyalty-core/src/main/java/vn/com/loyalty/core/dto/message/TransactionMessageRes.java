package vn.com.loyalty.core.dto.message;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.constant.enums.TransactionType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMessageRes implements Serializable {

    String transactionId;
    String customerCode;
    TransactionType transactionType;
    LocalDateTime transactionTime;
    BigDecimal transactionValue;
    BigDecimal epointGain;
    BigDecimal rpointGain;
    BigDecimal epointSpend;

}
