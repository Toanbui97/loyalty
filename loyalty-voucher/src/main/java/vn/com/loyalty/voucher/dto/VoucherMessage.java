package vn.com.loyalty.voucher.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class VoucherMessage extends OrchestrationMessage {
    String voucherCode;
    BigDecimal numberVoucher;
}
