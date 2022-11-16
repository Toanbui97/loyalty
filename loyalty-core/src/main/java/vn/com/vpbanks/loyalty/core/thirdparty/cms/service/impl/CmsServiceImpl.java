package vn.com.vpbanks.loyalty.core.thirdparty.cms.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.vpbanks.loyalty.core.configuration.propertires.WebClientProperties;
import vn.com.vpbanks.loyalty.core.constant.enums.ResponseStatusCode;
import vn.com.vpbanks.loyalty.core.exception.BaseResponseException;
import vn.com.vpbanks.loyalty.core.service.internal.WebClientCommonService;
import vn.com.vpbanks.loyalty.core.thirdparty.cms.dto.CreateUpdateRankCustomerRequest;
import vn.com.vpbanks.loyalty.core.thirdparty.cms.service.CmsService;
import vn.com.vpbanks.loyalty.core.utils.factory.response.BodyResponse;

@RequiredArgsConstructor
@Slf4j
@Service
public class CmsServiceImpl implements CmsService {

    private final WebClientCommonService webClientService;

    private final WebClientProperties webClientProperties;

    @Override
    public BodyResponse updateRankCustomer(CreateUpdateRankCustomerRequest requestBody) {

        BodyResponse response = new BodyResponse();
        response.setStatus(1);
        try {
            response = webClientService.postSync(webClientProperties.getCmsService().getBaseUrl()
                    , webClientProperties.getCmsService().getUpdateRankApi()
                    , requestBody, BodyResponse.class);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new BaseResponseException(ResponseStatusCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestBody.getRequestId(), null);
        }

        return response;
    }
}
