package vn.com.loyalty.core.entity.cms;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "epoint_spend", schema = "cms")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class EpointSpendEntity extends BaseEntity {

    String transactionId;
    String customerCode;
    @Builder.Default
    BigDecimal epoint = BigDecimal.ZERO;
    @Builder.Default
    LocalDate transactionDay = LocalDate.now();
    @Builder.Default
    String source = "NONE";

}
