package KUSITMS.WITHUS.global.infra.email.sender;

import jakarta.mail.MessagingException;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FakeMailSender implements MailSender {
    @Override
    public void send(String to, String subject, String text) {
        System.out.println("📲 이메일 발송: [" + to + "] → 메시지: " + text);
    }

    @Override
    public void sendWithAttachments(
            String to,
            String subject,
            String html,
            List<InputStreamSource> attachments
    ) {
        System.out.println("📲 이메일 발송(첨부파일 " + attachments.size() + "개): ["
                + to + "] 제목: " + subject + " 본문: " + html);
    }
}
