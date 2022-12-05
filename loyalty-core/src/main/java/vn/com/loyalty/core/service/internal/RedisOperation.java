package vn.com.loyalty.core.service.internal;

public interface RedisOperation {
    void setValue(String key, Object value);

    <T> T getValue(String key);

    boolean hasValue(String key);

    String genEpointKey(String customerCode);
}
