package vn.com.loyalty.transaction.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class VoucherMessage extends OrchestrationMessage {
    BigDecimal numberVoucher;
    String voucherCode;
}
