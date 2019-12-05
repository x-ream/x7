package io.xream.x7.demo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import x7.EnableX7L3Caching;
import x7.repository.cache.DefaultL3CacheStoragePolicy;
import x7.repository.cache.L3CacheStoragePolicy;
import x7.repository.cache.customizer.L3CacheStoragePolicyCustomizer;


@Configuration
@EnableX7L3Caching
public class L3RedisCacheConfig {

    @Value("${spring.redis.l3.database}")
    private int database;
    @Value("${spring.redis.l3.host}")
    private String hostName;
    @Value("${spring.redis.l3.port}")
    private int port;
    @Value("${spring.redis.l3.password}")
    private String password;


    @Bean
    public L3CacheStoragePolicyCustomizer l3CacheStoragePolicyCustomizer(){

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(hostName, port);
        config.setPassword(password);
        config.setDatabase(database);

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(config);
        connectionFactory.afterPropertiesSet();

        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        stringRedisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.afterPropertiesSet();

        return () -> {

            L3CacheStoragePolicy l3CacheStoragePolicy = new DefaultL3CacheStoragePolicy();
            ((DefaultL3CacheStoragePolicy) l3CacheStoragePolicy).setStringRedisTemplate(stringRedisTemplate);

            return l3CacheStoragePolicy;
        };
    }

}
