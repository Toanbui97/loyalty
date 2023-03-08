package vn.com.loyalty.core;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.redis.connection.RedisServer;
import vn.com.loyalty.core.configuration.propertires.RedisHostPortProperties;

@TestConfiguration
public class TestRedisConfiguration {

    private final RedisServer redisServer;

    public TestRedisConfiguration() {

        this.redisServer = new RedisServer("localhost", 6379);
    }
}
