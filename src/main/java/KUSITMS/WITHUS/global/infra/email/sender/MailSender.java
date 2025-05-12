package KUSITMS.WITHUS.global.infra.email.sender;

public interface MailSender {
    void send(String to, String subject, String text);
}
