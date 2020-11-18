package x7.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//@Configuration
public class BigDataRedisConfig {

    @Value("${bigdata.redis.host}")
    private String hostName;

    @Value("${bigdata.redis.port}")
    private int port;

    @Value("${bigdata.cache.redis.time-to-live.seconds}")
    private int entryTTL;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(hostName, port);
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(config);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }


    @Bean
    public BigDataRedisTemplate bigDataRedisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);

//        template.setKeySerializer(new ByteArraySerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();

        BigDataRedisTemplate bigDataRedisTemplate = new BigDataRedisTemplate(template);
        return bigDataRedisTemplate;
    }

}
