package KUSITMS.WITHUS.global.util.redis;

import java.time.Duration;

public interface VerificationCache {
    void saveCode(String identifier, String code, Duration ttl);
    String getCode(String identifier);
    void markVerified(String identifier, Duration ttl);
    boolean isVerified(String identifier);
}

