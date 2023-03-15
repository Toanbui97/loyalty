package vn.com.loyalty.core.service.internal;

import java.util.Collection;
import java.util.List;

public interface RedisOperation {
    <T> List<T> getValuesMatchPrefix(String keyPrefix, Class<T> clazz);

    void setValue(String key, Object value);

    <T> T getValue(String key, Class<T> clazz);

    <T> Collection<T> getListValue(String key, Class<T> clazz);

    void delete(String key);

    boolean hasValue(String key);

    String genEpointKey(String customerCode);

    String genRpointKey(String customerCode);

    void watchAndBegin(String... keys);

    void begin();
    void commit();

    void rollback();
}
