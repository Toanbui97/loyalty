package vn.com.vpbanks.loyalty.core.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import vn.com.vpbanks.loyalty.core.constant.Constants;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = Constants.ServiceConfig.KAFKA_POOL_PROPERTIES_PREFIX)
public class KafkaPoolPrefixDto {

    private Integer maxPollRecords;
    private Integer maxPollInterval;
}
