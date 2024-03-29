package vn.com.loyalty.core.dto.message;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionOrchestrationReq extends OrchestrationMessage {
    BigDecimal epointGain;
    BigDecimal epointSpend;
    BigDecimal rpointGain;
    transient List<String> voucherCodeList;

}
