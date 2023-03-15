package vn.com.loyalty.core.service.internal.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.dto.request.MasterDataRequest;
import vn.com.loyalty.core.dto.response.cms.MasterDataResponse;
import vn.com.loyalty.core.entity.MasterDataEntity;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.mapper.MasterDataMapper;
import vn.com.loyalty.core.repository.MasterDataRepository;
import vn.com.loyalty.core.service.internal.MasterDataService;
import vn.com.loyalty.core.service.internal.RedisOperation;

@Service
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {

    private final MasterDataRepository masterDataRepository;
    private final RedisOperation redisOperation;
    private final ObjectMapper objectMapper;
    private final MasterDataMapper masterDataMapper;

    @Override
    public <T> T getValue(String key, Class<T> clazz) {
        try {
            MasterDataEntity masterData = redisOperation.getValue(Constants.MasterDataKey.M_DATA_FOLDER + key, MasterDataEntity.class);
            return objectMapper.convertValue(masterData.getValue(), clazz);
        } catch (Exception e) {
            MasterDataEntity masterData = masterDataRepository.findByKey(key).orElseThrow(
                    () -> new ResourceNotFoundException(MasterDataEntity.class, key));
            return objectMapper.convertValue(masterData.getValue(), clazz);
        }
    }

    @Override
    @Transactional
    public MasterDataResponse setValue(MasterDataRequest request) {

        MasterDataEntity masterData = masterDataRepository.findByKey(request.getKey()).
                orElse(MasterDataEntity.builder().build());
        masterData.setKey(request.getKey());
        masterData.setValue(request.getValue());
        masterData = masterDataRepository.save(masterData);
        redisOperation.setValue(Constants.MasterDataKey.M_DATA_FOLDER+masterData.getKey(), masterData);
        return masterDataMapper.entityToDTO(masterData);
    }
}
