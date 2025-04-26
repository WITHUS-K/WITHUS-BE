package KUSITMS.WITHUS.global.infra.sms;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class CoolSmsSender implements SmsSender {

    private final DefaultMessageService messageService;

    public CoolSmsSender(
            @Value("${coolsms.api-key}") String apiKey,
            @Value("${coolsms.api-secret}") String apiSecret
    ) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    @Value("${coolsms.sender-number}")
    private String sender;

    @Override
    public void send(String phoneNumber, String message) {
        Message msg = new Message();
        msg.setFrom(sender);
        msg.setTo(phoneNumber);
        msg.setText(message);

        messageService.sendOne(new SingleMessageSendingRequest(msg));
    }
}
