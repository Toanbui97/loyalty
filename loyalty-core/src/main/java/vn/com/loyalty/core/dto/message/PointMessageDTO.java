package vn.com.loyalty.core.dto.message;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class PointMessageDTO implements Serializable {

    String customerCode;
    BigDecimal epoint;
    BigDecimal rpoint;
}
