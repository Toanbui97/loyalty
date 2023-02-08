package vn.com.loyalty.core.thirdparty.service;

import jdk.jfr.Frequency;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.dto.request.RankRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.loyalty.core.dto.response.cms.RankResponse;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;

import java.util.List;

public interface CmsWebClient extends WebClientService {

    BodyResponse<CustomerResponse> receiveCustomerInfo(BodyRequest<CustomerRequest> req);

    BodyResponse<CustomerResponse> performUpdateCustomerInfo(BodyRequest<CustomerRequest> req);

    BodyResponse<RankResponse> receiveRankList();
}
