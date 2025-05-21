package KUSITMS.WITHUS.domain.template.entity;

import KUSITMS.WITHUS.domain.template.enumerate.TemplateType;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TEMPLATE")
@Getter
@Setter
@NoArgsConstructor
public class Template {
    @Id
    @GeneratedValue
    Long id;

    @Column(nullable = false)
    private String name; // 템플릿 이름

    @Column(length = 10_000)
    private String subject;  // 제목 템플릿 (placeholders 허용)

    @Column(nullable = false, length = 100_000)
    private String body;  // HTML 본문 템플릿

    @Column(nullable = false)
    private TemplateType templateType;

    public Template(String name, String subject, String body, TemplateType templateType) {
        if(templateType == TemplateType.MAIL && (subject == null || subject.isBlank())) {
            throw new CustomException(ErrorCode.EMAIL_SUBJECT_REQUIRED);
        }
        this.name = name;
        this.subject = subject;
        this.body = body;
        this.templateType = templateType;
    }
}

