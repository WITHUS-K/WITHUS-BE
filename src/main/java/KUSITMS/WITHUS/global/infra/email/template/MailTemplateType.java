package KUSITMS.WITHUS.global.infra.email.template;

import lombok.Getter;

@Getter
public enum MailTemplateType {
    INTERVIEW_REQUEST("interview-request.html"),
    EVALUATION_REMINDER("evaluation-reminder.html"),
    INVITATION("invitation.html"),
    VERIFICATION("verification.html");

    private final String fileName;

    MailTemplateType(String fileName) {
        this.fileName = fileName;
    }
}
