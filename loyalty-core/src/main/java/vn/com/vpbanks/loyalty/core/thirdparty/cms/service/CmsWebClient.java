package vn.com.vpbanks.loyalty.core.thirdparty.cms.service;

import vn.com.vpbanks.loyalty.core.dto.request.BaseRequest;
import vn.com.vpbanks.loyalty.core.dto.request.CustomerRequest;
import vn.com.vpbanks.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.vpbanks.loyalty.core.thirdparty.cms.dto.CreateUpdateRankCustomerRequest;
import vn.com.vpbanks.loyalty.core.utils.factory.response.BodyResponse;

public interface CmsWebClient {
    BodyResponse<CustomerResponse> receiveCustomerInfo(BaseRequest<CustomerRequest> request);
    BodyResponse<CustomerResponse> performUpdateCustomerInfo(BaseRequest<CustomerRequest> request);
}
