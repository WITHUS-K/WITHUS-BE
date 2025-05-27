package KUSITMS.WITHUS.domain.recruitment.recruitment.entity;

import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationScaleType;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.entity.AvailableTimeRange;
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

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT", length = 1000)
    private String content;

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
    @Column(name = "NEED_MAJOR", nullable = false)
    private boolean needMajor = false;

    @Builder.Default
    @Column(name = "NEED_ACADEMIC_STATUS", nullable = false)
    private boolean needAcademicStatus = false;

    @Column(name = "DOCUMENT_DEADLINE")
    private LocalDate documentDeadline;

    @Builder.Default
    @Column(name = "IS_DOCUMENT_RESULT_REQUIRED", nullable = false)
    private boolean isDocumentResultRequired = true;

    @Column(name = "DOCUMENT_RESULT_DATE")
    private LocalDate documentResultDate;

    @Column(name = "FINAL_RESULT_DATE")
    private LocalDate finalResultDate;

    @Builder.Default
    @Column(name = "IS_INTERVIEW_REQUIRED", nullable = false)
    private boolean isInterviewRequired = true;

    @Column(name = "INTERVIEW_DURATION")
    private Short interviewDuration;

    @Column(name = "URL_SLUG", unique = true, nullable = false)
    private String urlSlug;

    @Builder.Default
    @Column(name = "IS_TEMPORARY", nullable = false)
    private boolean isTemporary = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "DOCUMENT_SCALE_TYPE")
    private EvaluationScaleType documentScaleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "INTERVIEW_SCALE_TYPE")
    private EvaluationScaleType interviewScaleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID", nullable = false)
    private Organization organization;

    @Builder.Default
    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationCriteria> evaluationCriteriaList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentQuestion> questions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailableTimeRange> availableTimeRanges = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Position> positions = new ArrayList<>();

    public static Recruitment create(
            String title,
            String content,
            LocalDate documentDeadline,
            boolean isDocumentResultRequired,
            LocalDate documentResultDate,
            LocalDate finalResultDate,
            boolean isInterviewRequired,
            Short interviewDuration,
            Organization organization,
            boolean needGender,
            boolean needAddress,
            boolean needSchool,
            boolean needBirthDate,
            boolean needAcademicStatus,
            boolean isTemporary,
            EvaluationScaleType documentScaleType,
            EvaluationScaleType interviewScaleType,
            String urlSlug
    ) {
        return Recruitment.builder()
                .title(title)
                .content(content)
                .documentDeadline(documentDeadline)
                .isDocumentResultRequired(isDocumentResultRequired)
                .documentResultDate(documentResultDate)
                .finalResultDate(finalResultDate)
                .isInterviewRequired(isInterviewRequired)
                .interviewDuration(interviewDuration)
                .organization(organization)
                .needGender(needGender)
                .needAddress(needAddress)
                .needSchool(needSchool)
                .needBirthDate(needBirthDate)
                .needAcademicStatus(needAcademicStatus)
                .isTemporary(isTemporary)
                .documentScaleType(documentScaleType)
                .interviewScaleType(interviewScaleType)
                .urlSlug(urlSlug)
                .build();
    }

    public void update(
            String title,
            String content,
            LocalDate documentDeadline,
            boolean isDocumentResultRequired,
            LocalDate documentResultDate,
            LocalDate finalResultDate,
            boolean isInterviewRequired,
            Short interviewDuration,
            boolean needGender,
            boolean needAddress,
            boolean needSchool,
            boolean needBirthDate,
            boolean needAcademicStatus,
            EvaluationScaleType documentScaleType,
            EvaluationScaleType interviewScaleType
    ) {
        this.title = title;
        this.content = content;
        this.documentDeadline = documentDeadline;
        this.isDocumentResultRequired = isDocumentResultRequired;
        this.documentResultDate = documentResultDate;
        this.finalResultDate = finalResultDate;
        this.isInterviewRequired = isInterviewRequired;
        this.interviewDuration = interviewDuration;
        this.needGender = needGender;
        this.needAddress = needAddress;
        this.needSchool = needSchool;
        this.needBirthDate = needBirthDate;
        this.needAcademicStatus = needAcademicStatus;
        this.documentScaleType = documentScaleType;
        this.interviewScaleType = interviewScaleType;
    }

    public void addDocumentQuestion(DocumentQuestion question) {
        this.questions.add(question);
        question.associateRecruitment(this);
    }

    public void addEvaluationCriteria(EvaluationCriteria criteria) {
        this.evaluationCriteriaList.add(criteria);
        criteria.associateRecruitment(this);
    }

    public void addAvailableTimeRange(AvailableTimeRange range) {
        this.availableTimeRanges.add(range);
        range.associateRecruitment(this);
    }

    public void addPosition(Position position) {
        this.positions.add(position);
        position.associateRecruitment(this);
    }

    public void clearEvaluationCriteria() {
        this.evaluationCriteriaList.clear();
    }

    public void clearDocumentQuestions() {
        this.questions.clear();
    }

    public void clearAvailableTimeRanges() {
        this.availableTimeRanges.clear();
    }

    public void markAsFinal() {
        this.isTemporary = false;
    }

    public void markAsTemporary() {
        this.isTemporary = true;
    }
}
