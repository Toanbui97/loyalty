package vn.com.loyalty.core.dto.kafka;

import com.fasterxml.classmate.AnnotationOverrides;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMessageDto implements Serializable {
    String transactionId;
    String customerCode;
    String transactionType;
    String transactionTime;
    Data data;

    @Getter
    @Setter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data implements Serializable {
        String transactionValue;
    }
}
