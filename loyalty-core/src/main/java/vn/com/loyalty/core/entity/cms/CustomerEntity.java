package vn.com.loyalty.core.entity.cms;

import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.util.StringUtils;
import vn.com.loyalty.core.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Table(name = "customer", schema = "cms")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class CustomerEntity extends BaseEntity {

    String customerName;
    String customerCode;
    @Builder.Default
    Long activeVoucher = 0L;
    @Builder.Default
    BigDecimal epoint = BigDecimal.ZERO;
    @Builder.Default
    BigDecimal rpoint = BigDecimal.ZERO;
    String rankCode = "NONE";
    LocalDate rankExpired = LocalDate.now();

}
