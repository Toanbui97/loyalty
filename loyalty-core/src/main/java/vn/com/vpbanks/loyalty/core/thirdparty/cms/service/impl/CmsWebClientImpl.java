package vn.com.vpbanks.loyalty.core.thirdparty.cms.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.vpbanks.loyalty.core.configuration.propertires.WebClientProperties;
import vn.com.vpbanks.loyalty.core.dto.request.BodyRequest;
import vn.com.vpbanks.loyalty.core.dto.request.CustomerRequest;
import vn.com.vpbanks.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.vpbanks.loyalty.core.entity.CustomerEntity;
import vn.com.vpbanks.loyalty.core.exception.ResourceNotFoundException;
import vn.com.vpbanks.loyalty.core.service.internal.WebClientCommonService;
import vn.com.vpbanks.loyalty.core.thirdparty.cms.service.CmsWebClient;
import vn.com.vpbanks.loyalty.core.utils.factory.response.BodyResponse;

@RequiredArgsConstructor
@Slf4j
@Service
public class CmsWebClientImpl implements CmsWebClient {

    private final WebClientCommonService webClientService;
    private final WebClientProperties webClientProperties;

    @Override
    public BodyResponse<CustomerResponse> receiveCustomerInfo(BodyRequest<CustomerRequest> req) {
        try {
            return webClientService.getSync(webClientProperties.getCmsService().getBaseUrl(),
                    webClientProperties.getCmsService().getReceiveCustomerInfo(),
                    null,
                    BodyResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResourceNotFoundException(CustomerEntity.class.getName(), req.getData().getCustomerCode());
        }
    }

    @Override
    public BodyResponse<CustomerResponse> performUpdateCustomerInfo(BodyRequest<CustomerRequest> req) {
        try {
            return webClientService.postSync(webClientProperties.getCmsService().getBaseUrl(),
                    webClientProperties.getCmsService().getPerformUpdateCustomerInfo(),
                    req,
                    BodyResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResourceNotFoundException(CustomerEntity.class.getName(), req.getData().getCustomerCode());
        }
    }
}
