package vn.com.loyalty.core.configuration.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import vn.com.loyalty.core.configuration.propertires.RedisHostPortProperties;

@Configuration
@EnableConfigurationProperties(RedisHostPortProperties.class)
@RequiredArgsConstructor
@EnableTransactionManagement
public class RedisConfiguration {

    private final RedisHostPortProperties redisHostPortProperties;
    private final ObjectMapper objectMapper;

    @Bean(destroyMethod = "shutdown")
    ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        return new RedisStandaloneConfiguration(redisHostPortProperties.getHost(), redisHostPortProperties.getPort());
    }

    @Bean
    public ClientOptions clientOptions(){
        return ClientOptions.builder()
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .autoReconnect(true)
                .build();
    }

    @Bean
    public LettucePoolingClientConfiguration lettucePoolConfig(ClientOptions clientOptions, ClientResources clientResources) {

        return LettucePoolingClientConfiguration.builder()
                .poolConfig(new GenericObjectPoolConfig())
                .clientOptions(clientOptions)
                .clientResources(clientResources)
                .build();
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration, LettucePoolingClientConfiguration lettucePoolingClientConfiguration) {
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettucePoolingClientConfiguration);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.setDefaultSerializer(new StringRedisSerializer());
        // sync with jdbc transaction
        template.setEnableTransactionSupport(true);

        template.afterPropertiesSet();

        return template;
    }

}
