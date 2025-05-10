package KUSITMS.WITHUS.global.infra.sms;

import org.springframework.stereotype.Component;

@Component
public class FakeSmsSender implements SmsSender {

    @Override
    public void send(String phoneNumber, String message) {
        System.out.println("📲 SMS 발송: [" + phoneNumber + "] → 메시지: " + message);
    }
}
