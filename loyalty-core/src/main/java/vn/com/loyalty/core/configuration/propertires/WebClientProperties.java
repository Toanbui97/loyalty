package vn.com.loyalty.core.configuration.propertires;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
public class WebClientProperties {

    CmsUrlInfo cmsService;

    @Data
    public static class CmsUrlInfo {
        String baseUrl;
        String updateRankApi;
        String receiveCustomerInfo;
        String performUpdateCustomer;
        String receiveRankList;
    }

}
