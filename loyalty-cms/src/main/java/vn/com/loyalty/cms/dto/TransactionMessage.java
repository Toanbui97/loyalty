package vn.com.loyalty.cms.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMessage extends OrchestrationMessage {
    BigDecimal epointGain;
    BigDecimal epointSpend;
    BigDecimal rpointGain;
    Long activeVoucher;
}
