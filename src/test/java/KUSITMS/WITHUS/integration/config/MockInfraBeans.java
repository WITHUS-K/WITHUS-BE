package KUSITMS.WITHUS.integration.config;

import KUSITMS.WITHUS.global.infra.email.sender.MailSender;
import KUSITMS.WITHUS.global.infra.sms.SmsSender;
import KUSITMS.WITHUS.global.infra.upload.uploader.Uploader;
import KUSITMS.WITHUS.util.FakeMailSender;
import KUSITMS.WITHUS.util.FakeSmsSender;
import KUSITMS.WITHUS.util.FakeUploader;
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
}


