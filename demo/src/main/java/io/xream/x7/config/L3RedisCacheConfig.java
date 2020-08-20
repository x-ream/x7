package io.xream.x7.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.xream.x7.EnableX7L3Caching;
import io.xream.x7.cache.L3CacheStorage;
import io.xream.x7.cache.customizer.L3CacheStorageCustomizer;
import io.xream.x7.repository.redis.cache.DefaultL3CacheStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


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
    public L3CacheStorageCustomizer l3CacheStorageCustomizer(){

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(hostName, port);
        config.setPassword(password);
        config.setDatabase(database);

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(config);
        connectionFactory.afterPropertiesSet();


        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(connectionFactory);
        stringRedisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());

        stringRedisTemplate.afterPropertiesSet();

        return () -> {

            L3CacheStorage l3CacheStorage = new DefaultL3CacheStorage();
            ((DefaultL3CacheStorage) l3CacheStorage).setStringRedisTemplate(stringRedisTemplate);

            return l3CacheStorage;
        };
    }

}
