package io.xream.x7.repository.redis.autoconfigure;

import io.xream.sqli.spi.L2CacheStorage;
import io.xream.x7.repository.redis.cache.DefaultL2CacheStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Author Sim
 */
public class L2CacheStorageAutoConfiguration {

    @ConditionalOnBean(StringRedisTemplate.class)
    @Bean
    public L2CacheStorage l2CacheStorage(){
        return new DefaultL2CacheStorage();
    }
}
