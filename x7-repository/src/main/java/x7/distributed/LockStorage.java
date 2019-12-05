package x7.distributed;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class LockStorage {

    private StringRedisTemplate stringRedisTemplate;

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
        DistributionLock.init(this);
    }

    public boolean lock(String key){

        boolean isLock;

        final String value = "LOCK";

        isLock = this.stringRedisTemplate.opsForValue().setIfAbsent(key, value);
        if (isLock) {
            this.stringRedisTemplate.expire(key,5, TimeUnit.SECONDS);
        }

        return isLock;
    }

    public void unLock(String key){
        this.stringRedisTemplate.delete(key);
    }
}
