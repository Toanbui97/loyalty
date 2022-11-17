package vn.com.vpbanks.loyalty.core.configuration.propertires;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "webclient")
public class WebClientProperties {

    CmsUrlInfo cmsService;

    @Data
    public static class CmsUrlInfo {
        String baseUrl;
        String updateRankApi;
        String receiveCustomerInfo;
        String performUpdateCustomer;
    }

}
