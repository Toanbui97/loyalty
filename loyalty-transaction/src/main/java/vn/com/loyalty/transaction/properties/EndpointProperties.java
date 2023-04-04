package vn.com.loyalty.transaction.properties;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
@Setter
public class EndpointProperties {

    private final CMSEndpoint cmsEndpoint;
    private final VoucherEndpoint voucherEndpoint;


    @Component
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @PropertySource("classpath:end-point.properties")
    @ConfigurationProperties(prefix = "cms")
    public static class CMSEndpoint {
        String baseUrl;
        String processOrchestrationTransaction;
        String rollbackOrchestrationTransaction;
        String processBuyVoucherOrchestration;
        String rollbackBuyVoucherOrchestration;
    }

    @Component
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @PropertySource("classpath:end-point.properties")
    @ConfigurationProperties(prefix = "voucher")
    public static class VoucherEndpoint {
        String baseUrl;
        String processOrchestrationTransaction;
        String rollbackOrchestrationTransaction;
        String processBuyVoucherOrchestration;
    }

}
