package KUSITMS.WITHUS.global.util.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RefreshTokenCacheUtil {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    // RefreshToken 저장
    public void saveRefreshToken(String email, String refreshToken, Duration ttl) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(key, refreshToken, ttl);
    }

    // RefreshToken 조회
    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + email);
    }

    // RefreshToken 삭제 (로그아웃용)
    public void deleteRefreshToken(String email) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + email);
    }
}
