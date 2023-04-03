package vn.com.loyalty.core.dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.com.loyalty.core.constant.Constants;

import java.io.Serializable;


@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class OrchestrationMessage implements Serializable {
    @NotNull
    String transactionId;
    @NotNull
    String customerCode;

}
