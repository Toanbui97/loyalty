package vn.com.loyalty.core.service.internal;

import vn.com.loyalty.core.service.internal.impl.RedisOperationImpl;

import java.util.List;

public interface RedisOperation {
    <T> List<?> getValuesMatchPrefix(String keyPrefix, Class<T> clazz);

    void setValue(String key, Object value);
//
//    <T> T getValue(String key);

//    <T> List<T> getValuesMatchPrefix(String keyPrefix);
//
//    <T> T getValue(String key, T clazz);

    <T> T getValue(String key, Class<T> clazz);

    boolean hasValue(String key);

    String genEpointKey(String customerCode);

    String genRpointKey(String customerCode);

    void watchAndBegin(String... keys);

    void begin();
    void commit();

    void rollback();
}
