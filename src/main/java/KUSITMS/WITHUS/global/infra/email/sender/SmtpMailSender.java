package KUSITMS.WITHUS.global.infra.email.sender;

import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class SmtpMailSender implements MailSender {

    private static final int MAX_ATTEMPTS = 3;
    private static final long RETRY_BACKOFF_MS = 500L;

    private final JavaMailSender javaMailSender;

    @Override
    public void send(String to, String subject, String text) {
        sendWithRetry(to, subject, () -> {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            javaMailSender.send(message);

            log.info("Email accepted by SMTP: [{}] subject: {}", to, subject);
        });
    }

    @Override
    public void sendWithAttachments(
            String to, String subject, String html,
            List<InputStreamSource> attachments
    ) throws MessagingException {
        sendWithRetry(to, subject, () -> {
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "utf-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            // ByteArrayResource 등 InputStreamSource 를 바로 첨부
            for (InputStreamSource src : attachments) {
                // ByteArrayResource 로 생성할 때 파일명을 override 해 두어야 함
                String filename = (src instanceof org.springframework.core.io.ByteArrayResource bar && bar.getFilename() != null)
                        ? bar.getFilename()
                        : "attachment";
                helper.addAttachment(filename, src);
            }

            javaMailSender.send(msg);
            log.info("Email accepted by SMTP: [{}] subject: {}", to, subject);
        });
    }

    private void sendWithRetry(String to, String subject, MailSendOperation operation) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                operation.send();
                return;
            } catch (MessagingException | MailException e) {
                lastException = e;
                log.warn(
                        "Email send attempt failed ({}/{}): [{}] subject: {}",
                        attempt,
                        MAX_ATTEMPTS,
                        to,
                        subject,
                        e
                );

                if (attempt < MAX_ATTEMPTS) {
                    sleepBeforeRetry();
                }
            }
        }

        log.error("Email send failed after retries: [{}] subject: {}", to, subject, lastException);
        throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
    }

    private void sleepBeforeRetry() {
        try {
            Thread.sleep(RETRY_BACKOFF_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
        }
    }

    @FunctionalInterface
    private interface MailSendOperation {
        void send() throws MessagingException;
    }
}
