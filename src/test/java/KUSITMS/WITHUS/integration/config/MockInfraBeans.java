package KUSITMS.WITHUS.integration.config;

import KUSITMS.WITHUS.global.infra.email.sender.MailSender;
import KUSITMS.WITHUS.global.infra.sms.SmsSender;
import KUSITMS.WITHUS.global.infra.upload.uploader.Uploader;
import KUSITMS.WITHUS.global.util.redis.RefreshTokenCacheUtil;
import KUSITMS.WITHUS.global.util.redis.VerificationCache;
import KUSITMS.WITHUS.util.FakeMailSender;
import KUSITMS.WITHUS.util.FakeSmsSender;
import KUSITMS.WITHUS.util.FakeUploader;
import KUSITMS.WITHUS.util.FakeVerificationCacheUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@TestConfiguration
public class MockInfraBeans {

    @Bean
    @Primary
    public MailSender mailSender() {
        return new FakeMailSender();
    }

    @Bean
    @Primary
    public SmsSender smsSender() {
        return new FakeSmsSender();
    }

    @Bean
    @Primary
    public Uploader uploader() {
        return new FakeUploader();
    }

    @Bean
    @Primary
    public VerificationCache verificationCache() {
        return new FakeVerificationCacheUtil();
    }

    @Bean
    @Primary
    public RefreshTokenCacheUtil refreshTokenCacheUtil() {
        return new RefreshTokenCacheUtil(null) {
            private final Map<String, String> store = new ConcurrentHashMap<>();

            @Override
            public void saveRefreshToken(String email, String refreshToken, Duration ttl) {
                store.put(email, refreshToken);
            }

            @Override
            public String getRefreshToken(String email) {
                return store.get(email);
            }

            @Override
            public void deleteRefreshToken(String email) {
                store.remove(email);
            }
        };
    }
}

