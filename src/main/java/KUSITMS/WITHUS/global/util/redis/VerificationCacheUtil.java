package KUSITMS.WITHUS.global.util.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class VerificationCacheUtil {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveCode(String identifier, String code, Duration ttl) {
        String key = "prefix:" + identifier + ":code";
        redisTemplate.opsForValue().set(key, code, ttl);
    }

    public String getCode(String identifier) {
        return redisTemplate.opsForValue().get("prefix:" + identifier + ":code");
    }

    public void markVerified(String identifier, Duration ttl) {
        redisTemplate.opsForValue().set("prefix:" + identifier + ":verified", "true", ttl);
    }

    public boolean isVerified(String identifier) {
        return "true".equals(redisTemplate.opsForValue().get("prefix:" + identifier + ":verified"));
    }
}

