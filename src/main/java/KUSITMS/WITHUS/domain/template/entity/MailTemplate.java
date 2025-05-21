package KUSITMS.WITHUS.domain.template.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MAIL_TEMPLATE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailTemplate {
    @Id
    @GeneratedValue
    Long id;

    @Column(nullable = false)
    private String name; // 템플릿 이름

    @Column(nullable = false, length = 10_000)
    private String subject;  // 메일 제목 템플릿 (placeholders 허용)

    @Column(nullable = false, length = 100_000)
    private String body;  // HTML 본문 템플릿
}

