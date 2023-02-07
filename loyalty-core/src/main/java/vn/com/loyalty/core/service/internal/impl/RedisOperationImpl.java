package vn.com.loyalty.core.service.internal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.service.internal.RedisOperation;

import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisOperationImpl implements RedisOperation {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String getValue(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean hasValue(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key)) && redisTemplate.opsForValue().get(key) != null;
    }

    @Override
    public String genEpointKey(String customerCode) {
        return Constants.RedisConstants.EPOINT_DIR + customerCode;
    }

    @Override
    public String genRpointKey(String customerCode) {
        return Constants.RedisConstants.RPOINT_DIR + customerCode;
    }

    @Override
    public void rollback() {
        redisTemplate.discard();
    }

    @Override
    public void watchAndBegin(String... keys) {
        redisTemplate.watch(Arrays.stream(keys).toList());
        redisTemplate.multi();
    }

    @Override
    public void begin() {
        redisTemplate.multi();
    }

    @Override
    public void commit() {
        redisTemplate.exec();
    }
}