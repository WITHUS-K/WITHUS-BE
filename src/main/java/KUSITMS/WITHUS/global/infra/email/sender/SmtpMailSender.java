package KUSITMS.WITHUS.global.infra.email.sender;

import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class SmtpMailSender implements MailSender {

    private final JavaMailSender javaMailSender;

    @Override
    @Async("taskExecutor")
    public void send(String to, String subject, String text) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            javaMailSender.send(message);

            System.out.println("📩 이메일 발송: [" + to + "] → 제목: " + subject);
        } catch (MessagingException e) {
            log.error("Error sending email", e);
            throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
        }
    }

    @Override
    @Async
    public void sendWithAttachments(
            String to, String subject, String html,
            List<InputStreamSource> attachments
    ) throws MessagingException {
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
        log.info("📩 이메일 발송: [{}] → 제목: {}", to, subject);
    }
}