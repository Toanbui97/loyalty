package vn.com.loyalty.core.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerRequest implements Serializable {

    String customerName;
    String customerCode;
    Long activeVoucher;
    BigDecimal epoint;
    BigDecimal rpoint;
    BigDecimal gainEpoint;
    BigDecimal gainEloy;
    BigDecimal spendPoint;
}
