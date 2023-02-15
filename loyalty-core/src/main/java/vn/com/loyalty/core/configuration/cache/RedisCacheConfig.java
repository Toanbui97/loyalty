package vn.com.loyalty.core.configuration.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
@Slf4j
@RequiredArgsConstructor
public class RedisCacheConfig implements CachingConfigurer {

    private final RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer(Object.class)
                ));
    }

    @Override
    @Bean
    public CacheManager cacheManager() {
        return this.redisCacheManager(redisConnectionFactory, this.redisCacheConfiguration());
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory, RedisCacheConfiguration  redisCacheConfiguration) {
        return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(redisCacheConfiguration)
                .build();
    }

}
