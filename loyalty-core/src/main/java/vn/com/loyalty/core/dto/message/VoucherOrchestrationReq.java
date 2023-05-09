package vn.com.loyalty.core.dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class VoucherOrchestrationReq implements Serializable {
    @NotNull
    String transactionId;
    @NotNull
    String customerCode;
    BigDecimal numberVoucher;
    String voucherCode;
    BigDecimal epointSpend;
}
