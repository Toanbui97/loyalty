package vn.com.loyalty.core.entity.transaction;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.core.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "transaction", schema = "transaction")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class TransactionEntity extends BaseEntity {

    String transactionId;
    String customerCode;
    TransactionType transactionType;
    LocalDateTime transactionTime;
    BigDecimal transactionValue;
    BigDecimal epointGain;
    BigDecimal rpointGain;
    BigDecimal epointSpend;
    @Transient
    transient List<String> voucherDetailCodeList;
}
