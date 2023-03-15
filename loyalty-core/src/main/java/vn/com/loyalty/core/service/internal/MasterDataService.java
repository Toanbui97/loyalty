package vn.com.loyalty.core.service.internal;

import lombok.SneakyThrows;
import vn.com.loyalty.core.dto.request.MasterDataRequest;
import vn.com.loyalty.core.dto.response.cms.MasterDataResponse;

public interface MasterDataService {
    @SneakyThrows
    <T> T getValue(String key, Class<T> clazz);

    MasterDataResponse setValue(MasterDataRequest request);
}