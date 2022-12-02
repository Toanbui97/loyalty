package vn.com.loyalty.core.thirdparty.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.configuration.propertires.WebClientProperties;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.service.internal.WebClientCommonService;
import vn.com.loyalty.core.utils.RequestUtil;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.thirdparty.service.CmsWebClient;

@RequiredArgsConstructor
@Slf4j
@Service
public class CmsWebClientImpl implements CmsWebClient {

    private final WebClientCommonService webClientService;
    private final WebClientProperties webClientProperties;

    @Override
    public BodyResponse<CustomerResponse> receiveCustomerInfo(BodyRequest<CustomerRequest> req) {
        try {
            return webClientService.postSync(webClientProperties.getCmsService().getBaseUrl(),
                    RequestUtil.insertValueForPathURI(webClientProperties.getCmsService().getReceiveCustomerInfo(), req.getData().getCustomerCode()),
                    req,
                    BodyResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResourceNotFoundException(CustomerEntity.class, req.getData().getCustomerCode());
        }
    }

    @Override
    public BodyResponse<CustomerResponse> performUpdateCustomerInfo(BodyRequest<CustomerRequest> req) {
        try {
            return webClientService.postSync(webClientProperties.getCmsService().getBaseUrl(),
                    webClientProperties.getCmsService().getPerformUpdateCustomer(),
                    req,
                    BodyResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResourceNotFoundException(CustomerEntity.class, req.getData().getCustomerCode());
        }
    }
}
