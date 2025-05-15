package KUSITMS.WITHUS.util;

import KUSITMS.WITHUS.global.util.redis.VerificationCacheUtil;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class FakeVerificationCacheUtil extends VerificationCacheUtil {

    private final Map<String, String> store = new HashMap<>();

    public FakeVerificationCacheUtil() {
        super(null);
    }

    @Override
    public void saveCode(String identifier, String code, Duration ttl) {
        store.put("prefix:" + identifier + ":code", code);
    }

    @Override
    public String getCode(String identifier) {
        return store.get("prefix:" + identifier + ":code");
    }

    @Override
    public void markVerified(String identifier, Duration ttl) {
        store.put("prefix:" + identifier + ":verified", "true");
    }

    @Override
    public boolean isVerified(String identifier) {
        return "true".equals(store.get("prefix:" + identifier + ":verified"));
    }
}

