package KUSITMS.WITHUS.domain.application.applicationAnswer.entity;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "APPLICATION_ANSWER")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPLICATION_ANSWER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    private DocumentQuestion question;

    @Column(name = "ANSWER_TEXT")
    private String answerText;

    @Column(name = "FILE_URL")
    private String fileUrl;

    public static ApplicationAnswer create(Application app, DocumentQuestion question, String answerText, String fileUrl) {
        return ApplicationAnswer.builder()
                .application(app)
                .question(question)
                .answerText(answerText)
                .fileUrl(fileUrl)
                .build();
    }
}
