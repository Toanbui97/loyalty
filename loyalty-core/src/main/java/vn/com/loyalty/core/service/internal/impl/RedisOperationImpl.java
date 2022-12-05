package vn.com.loyalty.core.service.internal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.service.internal.RedisOperation;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisOperationImpl implements RedisOperation {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public <T> T getValue(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean hasValue(String key) {
        return redisTemplate.hasKey(key) && redisTemplate.opsForValue().get(key) != null;
    }

    @Override
    public String genEpointKey(String customerCode) {
        return Constants.RedisConstants.EPOINT_DIR + customerCode;
    }


}