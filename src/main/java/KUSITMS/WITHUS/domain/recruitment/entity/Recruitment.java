package KUSITMS.WITHUS.domain.recruitment.entity;

import KUSITMS.WITHUS.domain.application.template.entity.ApplicationTemplate;
import KUSITMS.WITHUS.domain.evaluation.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.organization.entity.Organization;
import KUSITMS.WITHUS.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RECRUITMENT")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Recruitment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECRUITMENT_ID")
    private Long id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "CONTENT", nullable = false, length = 1000)
    private String content;

    @Column(name = "FILE_URL")
    private String fileUrl;

    @Column(name = "DOCUMENT_DEADLINE", nullable = false)
    private LocalDate documentDeadline;

    @Column(name = "DOCUMENT_RESULT_DATE", nullable = false)
    private LocalDate documentResultDate;

    @Column(name = "FINAL_RESULT_DATE", nullable = false)
    private LocalDate finalResultDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID", nullable = false)
    private Organization organization;

    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EvaluationCriteria> evaluationCriteriaList = new ArrayList<>();

    @OneToOne(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    private ApplicationTemplate applicationTemplate;

    public static Recruitment create(
            String title,
            String content,
            String fileUrl,
            LocalDate documentDeadline,
            LocalDate documentResultDate,
            LocalDate finalResultDate,
            Organization organization
    ) {
        return Recruitment.builder()
                .title(title)
                .content(content)
                .fileUrl(fileUrl)
                .documentDeadline(documentDeadline)
                .documentResultDate(documentResultDate)
                .finalResultDate(finalResultDate)
                .organization(organization)
                .build();
    }

    public void update(
            String title,
            String content,
            String fileUrl,
            LocalDate documentDeadline,
            LocalDate documentResultDate,
            LocalDate finalResultDate
    ) {
        this.title = title;
        this.content = content;
        this.fileUrl = fileUrl;
        this.documentDeadline = documentDeadline;
        this.documentResultDate = documentResultDate;
        this.finalResultDate = finalResultDate;
    }

    public void setApplicationTemplate(ApplicationTemplate template) {
        this.applicationTemplate = template;
        template.associateRecruitment(this);
    }

    public void addEvaluationCriteria(EvaluationCriteria criteria) {
        this.evaluationCriteriaList.add(criteria);
        criteria.associateRecruitment(this);
    }
}
