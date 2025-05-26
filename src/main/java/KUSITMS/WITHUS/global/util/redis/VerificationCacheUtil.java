package KUSITMS.WITHUS.global.util.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class VerificationCacheUtil implements VerificationCache {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveCode(String identifier, String code, Duration ttl) {
        String key = "prefix:" + identifier + ":code";
        redisTemplate.opsForValue().set(key, code, ttl);
    }

    @Override
    public String getCode(String identifier) {
        return redisTemplate.opsForValue().get("prefix:" + identifier + ":code");
    }

    @Override
    public void markVerified(String identifier, Duration ttl) {
        redisTemplate.opsForValue().set("prefix:" + identifier + ":verified", "true", ttl);
    }

    @Override
    public boolean isVerified(String identifier) {
        return "true".equals(redisTemplate.opsForValue().get("prefix:" + identifier + ":verified"));
    }
}

