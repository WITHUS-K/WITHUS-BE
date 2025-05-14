package KUSITMS.WITHUS.global.infra.sms;

public interface SmsSender {
    void send(String phoneNumber, String message);
    void sendMms(String phoneNumber, String message, byte[] imageBytes, String filename);
}
