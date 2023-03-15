package vn.com.loyalty.core.dto.message;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import vn.com.loyalty.core.utils.DateTimeUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMessage implements Serializable {
    
    String transactionId;
    String customerCode;
    String transactionType;
    @DateTimeFormat(pattern = DateTimeUtils.ISO_8601_FORMAT)
    LocalDateTime transactionTime;
    Data data;

    @Getter
    @Setter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data implements Serializable {
        BigDecimal transactionValue;
        BigDecimal pointToDiscount;
        transient List<String> voucherDetailCodeList;
    }

}
