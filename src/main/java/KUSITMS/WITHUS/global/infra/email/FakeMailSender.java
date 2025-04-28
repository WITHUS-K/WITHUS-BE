package KUSITMS.WITHUS.global.infra.email;

import org.springframework.stereotype.Component;

@Component
public class FakeMailSender implements MailSender {
    @Override
    public void send(String to, String subject, String text) {
        System.out.println("📲 이메일 발송: [" + to + "] → 메시지: " + text);
    }
}
