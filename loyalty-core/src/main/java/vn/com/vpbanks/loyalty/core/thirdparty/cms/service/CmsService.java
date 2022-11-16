package vn.com.vpbanks.loyalty.core.thirdparty.cms.service;

import vn.com.vpbanks.loyalty.core.thirdparty.cms.dto.CreateUpdateRankCustomerRequest;
import vn.com.vpbanks.loyalty.core.utils.factory.response.BodyResponse;

public interface CmsService {
    BodyResponse updateRankCustomer(CreateUpdateRankCustomerRequest requestBody);
}
