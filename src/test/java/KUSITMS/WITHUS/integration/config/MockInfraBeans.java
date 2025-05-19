package KUSITMS.WITHUS.integration.config;

import KUSITMS.WITHUS.global.auth.jwt.util.JwtUtil;
import KUSITMS.WITHUS.global.infra.email.sender.MailSender;
import KUSITMS.WITHUS.global.infra.sms.SmsSender;
import KUSITMS.WITHUS.global.infra.upload.uploader.Uploader;
import KUSITMS.WITHUS.global.util.redis.VerificationCache;
import KUSITMS.WITHUS.util.FakeMailSender;
import KUSITMS.WITHUS.util.FakeSmsSender;
import KUSITMS.WITHUS.util.FakeUploader;
import KUSITMS.WITHUS.util.FakeVerificationCacheUtil;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

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
    public JwtUtil jwtUtil() {
        return Mockito.mock(JwtUtil.class);
    }

}


