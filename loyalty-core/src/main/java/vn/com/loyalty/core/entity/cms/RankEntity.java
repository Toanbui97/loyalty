package vn.com.loyalty.core.entity.cms;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;


@Table(name = "rank", schema = "cms")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class RankEntity extends BaseEntity implements Serializable {

    String rankCode;
    String rankName;
    BigDecimal requirePoint;
    BigDecimal keepPoint;
}
