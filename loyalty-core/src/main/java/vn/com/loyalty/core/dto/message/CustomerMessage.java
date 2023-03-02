package vn.com.loyalty.core.dto.message;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

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
    Data data;

    @Getter
    @Setter
    @SuperBuilder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data implements Serializable {
        BigDecimal epointGain;
        BigDecimal epointSpend;
        BigDecimal rpointGain;
        Long activeVoucher;
    }

}
