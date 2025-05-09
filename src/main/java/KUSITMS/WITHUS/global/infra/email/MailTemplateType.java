package KUSITMS.WITHUS.global.infra.email;

import lombok.Getter;

@Getter
public enum MailTemplateType {
    INTERVIEW_REQUEST("interview-request.html");

    private final String fileName;

    MailTemplateType(String fileName) {
        this.fileName = fileName;
    }
}
