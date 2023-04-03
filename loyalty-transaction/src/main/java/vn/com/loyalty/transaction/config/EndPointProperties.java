package vn.com.loyalty.transaction.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:end-point.yaml")
public class EndPointProperties {

    CMSService cmsService;
    VoucherService voucherService;

    @Data
    public static class CMSService {
        String baseUrl;
        String updateRankApi;
        String receiveCustomerInfo;
        String performUpdateCustomer;
        String receiveRankList;
        String processOrchestrationTransaction;
        String rollbackOrchestrationTransaction;
        String processBuyVoucherOrchestration;
        String rollbackBuyVoucherOrchestration;
    }
    @Data
    public static class VoucherService {
        String baseUrl;
        String updateRankApi;
        String receiveCustomerInfo;
        String performUpdateCustomer;
        String receiveRankList;
        String processOrchestrationTransaction;
        String rollbackOrchestrationTransaction;
        String processBuyVoucherOrchestration;
        String rollbackBuyVoucherOrchestration;
    }
}
