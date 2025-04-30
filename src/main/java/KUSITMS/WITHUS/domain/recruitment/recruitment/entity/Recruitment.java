package KUSITMS.WITHUS.domain.recruitment.recruitment.entity;

import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
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

    @Builder.Default
    @Column(name = "NEED_GENDER", nullable = false)
    private boolean needGender = false;

    @Builder.Default
    @Column(name = "NEED_ADDRESS", nullable = false)
    private boolean needAddress = false;

    @Builder.Default
    @Column(name = "NEED_SCHOOL", nullable = false)
    private boolean needSchool = false;

    @Builder.Default
    @Column(name = "NEED_BIRTH_DATE", nullable = false)
    private boolean needBirthDate = false;

    @Builder.Default
    @Column(name = "NEED_ACADEMIC_STATUS", nullable = false)
    private boolean needAcademicStatus = false;

    @Column(name = "DOCUMENT_DEADLINE", nullable = false)
    private LocalDate documentDeadline;

    @Column(name = "DOCUMENT_RESULT_DATE", nullable = false)
    private LocalDate documentResultDate;

    @Column(name = "FINAL_RESULT_DATE", nullable = false)
    private LocalDate finalResultDate;

    @Builder.Default
    @Column(name = "IS_TEMPORARY", nullable = false)
    private boolean isTemporary = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID", nullable = false)
    private Organization organization;

    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EvaluationCriteria> evaluationCriteriaList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentQuestion> questions = new ArrayList<>();

    public static Recruitment create(
            String title,
            String content,
            String fileUrl,
            LocalDate documentDeadline,
            LocalDate documentResultDate,
            LocalDate finalResultDate,
            Organization organization,
            boolean needGender,
            boolean needAddress,
            boolean needSchool,
            boolean needBirthDate,
            boolean needAcademicStatus,
            boolean isTemporary
    ) {
        return Recruitment.builder()
                .title(title)
                .content(content)
                .fileUrl(fileUrl)
                .documentDeadline(documentDeadline)
                .documentResultDate(documentResultDate)
                .finalResultDate(finalResultDate)
                .organization(organization)
                .needGender(needGender)
                .needAddress(needAddress)
                .needSchool(needSchool)
                .needBirthDate(needBirthDate)
                .needAcademicStatus(needAcademicStatus)
                .isTemporary(isTemporary)
                .build();
    }

    public void update(
            String title,
            String content,
            String fileUrl,
            LocalDate documentDeadline,
            LocalDate documentResultDate,
            LocalDate finalResultDate,
            boolean needGender,
            boolean needAddress,
            boolean needSchool,
            boolean needBirthDate,
            boolean needAcademicStatus
    ) {
        this.title = title;
        this.content = content;
        this.fileUrl = fileUrl;
        this.documentDeadline = documentDeadline;
        this.documentResultDate = documentResultDate;
        this.finalResultDate = finalResultDate;
        this.needGender = needGender;
        this.needAddress = needAddress;
        this.needSchool = needSchool;
        this.needBirthDate = needBirthDate;
        this.needAcademicStatus = needAcademicStatus;
    }

    public void addDocumentQuestion(DocumentQuestion question) {
        this.questions.add(question);
        question.associateRecruitment(this);
    }

    public void addEvaluationCriteria(EvaluationCriteria criteria) {
        this.evaluationCriteriaList.add(criteria);
        criteria.associateRecruitment(this);
    }

    public void markAsFinal() {
        this.isTemporary = false;
    }

    public void markAsTemporary() {
        this.isTemporary = true;
    }
}
