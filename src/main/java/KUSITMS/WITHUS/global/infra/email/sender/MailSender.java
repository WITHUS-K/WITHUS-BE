package KUSITMS.WITHUS.global.infra.email.sender;

import jakarta.mail.MessagingException;
import org.springframework.core.io.InputStreamSource;

import java.util.List;

public interface MailSender {
    void send(String to, String subject, String text);
    void sendWithAttachments(String to, String subject, String html, List<InputStreamSource> attachments) throws MessagingException;
}
