package KUSITMS.WITHUS.global.infra.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    private String provider = "smtp";
    /** provider=noop 일 때 실제 발송 대신 흉내낼 지연(ms). 부하 테스트용. */
    private long noopDelayMs = 0L;
    private String fromEmail;
    private String fromName = "WITHUS";
    private String sendgridApiKey;
    private String sendgridEndpoint = "https://api.sendgrid.com/v3/mail/send";
    private String sendgridEventWebhookPublicKey;
    private String domain;
    private String logoUrl;
    private String interviewerAvailabilityUrl;
    private String inviteUserUrl;
    private String invitationApiPath;
}
