package vn.com.loyalty.core.service.internal.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.exception.RedisException;
import vn.com.loyalty.core.service.internal.RedisOperation;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisOperationImpl implements RedisOperation {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> List<T> getValuesMatchPrefix(String keyPrefix, Class<T> clazz) {
        try {
            Set<String> keys = redisTemplate.keys(keyPrefix + "*");
            JavaType type = objectMapper.constructType(clazz);
            List<T> resultList = new ArrayList<>();
            List<Object> values = redisTemplate.opsForValue().multiGet(Objects.requireNonNull(keys));

            if (!CollectionUtils.isEmpty(values)) {
                for (Object value : values) {
                    resultList.add(objectMapper.convertValue(value, type));
                }
            }
            return resultList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RedisException(e.getMessage());
        }
    }

    @Override
    @SneakyThrows
    public void setValue(String key, Object value) {
        if (value instanceof String) {
            redisTemplate.opsForValue().set(key, String.valueOf(value));
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    @Override
    public <T> T getValue(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return objectMapper.convertValue(value, clazz);
        } catch (Exception e) {
            throw new RedisException(e.getMessage());

        }
    }

    @Override
    public <T> Collection<T> getListValue(String key, Class<T> clazz) {
       try {
           Object json = redisTemplate.opsForValue().get(key);
           JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
           return objectMapper.convertValue(json, type);
       } catch (Exception e) {
           throw new RedisException(e.getMessage());
       }
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new RedisException(e.getMessage());
        }
    }

    @Override
    public boolean hasValue(String key) {
        return redisTemplate.opsForValue().get(key) != null;
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