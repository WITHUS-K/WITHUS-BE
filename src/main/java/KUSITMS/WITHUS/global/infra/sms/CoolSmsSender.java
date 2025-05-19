package KUSITMS.WITHUS.global.infra.sms;

import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.model.StorageType;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Primary
@Slf4j
@Component
@Profile("!test")
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

        System.out.println("📲 SMS 발송: [" + phoneNumber + "] → 메시지: " + message);
    }

    @Override
    public void sendMms(
            String phoneNumber,
            String message,
            byte[] imageBytes,
            String filename
    ) {
        try {
            // 메모리 바이트 → 톰캣 임시파일이 아닌, JVM temp 디렉토리에 새로 생성
            Path tempFile = Files.createTempFile("mms-", "-" + filename);
            Files.write(tempFile, imageBytes);

            String imageId = messageService.uploadFile(
                    tempFile.toFile(), StorageType.MMS, null
            );

            Message msg = new Message();
            msg.setFrom(sender);
            msg.setTo(phoneNumber);
            msg.setText(message);
            msg.setImageId(imageId);

            messageService.sendOne(new SingleMessageSendingRequest(msg));

            Files.deleteIfExists(tempFile);

            System.out.println("📲 SMS 발송: [" + phoneNumber + "] → 메시지: " + message);

        } catch (IOException e) {
            throw new CustomException(ErrorCode.SMS_ATTACHMENT_PROCESS_FAIL);
        } catch (RuntimeException e) { // SDK 내부에서 던지는 unchecked 예외
            throw new CustomException(ErrorCode.SMS_SEND_FAIL);
        }
    }

}
