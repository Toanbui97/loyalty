package vn.com.loyalty.core.thirdparty.service;

import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;

public interface CmsWebClient extends WebClientService {

    BodyResponse<CustomerResponse> receiveCustomerInfo(BodyRequest<CustomerRequest> req);

    BodyResponse<CustomerResponse> performUpdateCustomerInfo(BodyRequest<CustomerRequest> request);
}
