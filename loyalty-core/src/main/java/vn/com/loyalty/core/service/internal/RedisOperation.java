package vn.com.loyalty.core.service.internal;

public interface RedisOperation {
    void setValue(String key, String value);

    String getValue(String key);

    boolean hasValue(String key);

    String genEpointKey(String customerCode);

    String genRpointKey(String customerCode);

    void watchAndBegin(String... keys);

    void begin();
    void commit();

    void rollback();
}
