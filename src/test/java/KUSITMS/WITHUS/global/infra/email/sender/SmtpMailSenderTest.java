package KUSITMS.WITHUS.global.infra.email.sender;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmtpMailSenderTest {

    @Test
    @DisplayName("단건 메일 전송은 호출자가 SMTP 전송 결과를 알 수 있도록 비동기로 실행하지 않는다")
    void sendIsNotAsync() throws Exception {
        boolean hasAsyncAnnotation = SmtpMailSender.class
                .getMethod("send", String.class, String.class, String.class)
                .isAnnotationPresent(Async.class);

        assertThat(hasAsyncAnnotation).isFalse();
    }

    @Test
    @DisplayName("SMTP 전송이 일시 실패하면 재시도 후 성공 처리한다")
    void sendRetriesTransientMailException() {
        JavaMailSender javaMailSender = mock(JavaMailSender.class);
        SmtpMailSender mailSender = new SmtpMailSender(javaMailSender);
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));

        when(javaMailSender.createMimeMessage()).thenReturn(message);
        doThrow(new MailSendException("temporary failure"))
                .doNothing()
                .when(javaMailSender)
                .send(any(MimeMessage.class));

        mailSender.send("receiver@example.com", "subject", "<p>body</p>");

        verify(javaMailSender, org.mockito.Mockito.times(2)).createMimeMessage();
        verify(javaMailSender, org.mockito.Mockito.times(2)).send(any(MimeMessage.class));
    }
}
