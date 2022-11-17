package vn.com.vpbanks.loyalty.core.thirdparty.cms.service;

import vn.com.vpbanks.loyalty.core.dto.request.BodyRequest;
import vn.com.vpbanks.loyalty.core.dto.request.CustomerRequest;
import vn.com.vpbanks.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.vpbanks.loyalty.core.utils.factory.response.BodyResponse;

public interface CmsWebClient {
    BodyResponse<CustomerResponse> receiveCustomerInfo(String customerCode);
    BodyResponse<CustomerResponse> performUpdateCustomerInfo(BodyRequest<CustomerRequest> request);
}
