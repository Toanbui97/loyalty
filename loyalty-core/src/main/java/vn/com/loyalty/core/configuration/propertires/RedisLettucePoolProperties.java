package vn.com.loyalty.core.configuration.propertires;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.redis.lettuce.pool")
public class RedisLettucePoolProperties {
    private Integer minIdle;
    private Integer numTestPerEvictionRun;
    private Integer timeBetweenEvictionRuns;
}
