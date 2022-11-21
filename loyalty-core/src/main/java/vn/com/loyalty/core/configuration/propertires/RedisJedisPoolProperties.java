package vn.com.loyalty.core.configuration.propertires;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.redis.jedis.pool")
public class RedisJedisPoolProperties {
    private Integer maxActive;
    private Integer maxIdle;
    private Integer minIdle;
    private Boolean testOnBorrow;
    private Boolean testOnReturn;
    private Boolean testWhileIdle;
    private Boolean blockWhenExhausted;
    private Integer maxWait;
}
