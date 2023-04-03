package vn.com.loyalty.cms.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;

@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TransactionResponse extends OrchestrationMessage {
}
