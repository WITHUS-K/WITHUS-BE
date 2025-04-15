package KUSITMS.WITHUS.domain.application.entity;

import KUSITMS.WITHUS.domain.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "APPLICATION_TEMPLATE")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEMPLATE_ID")
    private Long id;

    @Column(nullable = false)
    private String title;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUITMENT_ID", nullable = false)
    private Recruitment recruitment;

    @Builder.Default
    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationQuestion> questions = new ArrayList<>();

    public void associateRecruitment(Recruitment recruitment) {
        this.recruitment = recruitment;
    }

    public void addQuestion(ApplicationQuestion question) {
        this.questions.add(question);
        question.associateTemplate(this);
    }
}
