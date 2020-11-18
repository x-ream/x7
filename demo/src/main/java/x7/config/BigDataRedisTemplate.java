package x7.config;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @Author Sim
 */
public class BigDataRedisTemplate {

    private RedisTemplate redisTemplate;

    public BigDataRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean hset(byte[] hash, byte[] key, byte[] value) {
        this.redisTemplate.opsForHash().put(hash,key,value);
        return true;
    }

    public Object hget(byte[] hash,byte[] key) {
        return this.redisTemplate.opsForHash().get(hash,key);
    }
    // 没找到其他的方法,可能通过这个方法可以全部取出来
    public Object get(byte[] hash) {
        return this.redisTemplate.opsForValue().get(hash);
    }

    public boolean set(byte[] hash, byte[] value) {
        this.redisTemplate.opsForValue().set(hash,value);
        return true;
    }

    public boolean set(byte[] hash, byte[] value, long time, TimeUnit timeUnit) {
        this.redisTemplate.opsForValue().set(hash,value,time,timeUnit);
        return true;
    }
}
