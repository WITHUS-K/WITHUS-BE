package KUSITMS.WITHUS.global.util.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class PhoneAuthCacheUtil {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveCode(String phoneNumber, String code, Duration ttl) {
        String key = "phone:" + phoneNumber + ":code";
        redisTemplate.opsForValue().set(key, code, ttl);
    }

    public String getCode(String phoneNumber) {
        return redisTemplate.opsForValue().get("phone:" + phoneNumber + ":code");
    }

    public void markVerified(String phoneNumber, Duration ttl) {
        redisTemplate.opsForValue().set("phone:" + phoneNumber + ":verified", "true", ttl);
    }

    public boolean isVerified(String phoneNumber) {
        return "true".equals(redisTemplate.opsForValue().get("phone:" + phoneNumber + ":verified"));
    }
}

