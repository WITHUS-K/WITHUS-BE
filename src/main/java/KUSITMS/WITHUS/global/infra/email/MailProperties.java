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
    private String domain;
    private String logoUrl;
    private String interviewerAvailabilityUrl;
    private String inviteUserUrl;
    private String invitationApiPath;
}

