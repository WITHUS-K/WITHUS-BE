package KUSITMS.WITHUS.domain.application.application.entity;

import KUSITMS.WITHUS.domain.application.application.enumerate.AcademicStatus;
import KUSITMS.WITHUS.domain.application.applicationAcquaintance.entity.ApplicationAcquaintance;
import KUSITMS.WITHUS.domain.application.applicationAnswer.entity.ApplicationAnswer;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.entity.ApplicationEvaluator;
import KUSITMS.WITHUS.domain.application.applicantAvailability.entity.ApplicantAvailability;
import KUSITMS.WITHUS.domain.application.comment.entity.Comment;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import KUSITMS.WITHUS.domain.interview.interviewQuestion.entity.InterviewQuestion;
import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.common.BaseEntity;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "APPLICATION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPLICATION_ID")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String email;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;

    private String university;

    private String major;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACADEMIC_STATUS")
    private AcademicStatus academicStatus;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    private String address;

    @Builder.Default
    @Column(name = "IS_MAIL_SENT")
    private Boolean isMailSent = false;

    @Builder.Default
    @Column(name = "IS_SMS_SENT")
    private Boolean isSmsSent = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUITMENT_ID", nullable = false)
    private Recruitment recruitment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_ID")
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTERVIEW_ID")
    private Interview interview;

    @Builder.Default
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicantAvailability> availabilities = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewQuestion> interviewQuestions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluation> evaluations = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationEvaluator> evaluators;

    @Builder.Default
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationAnswer> answers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationAcquaintance> acquaintances = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIME_SLOT_ID")
    private TimeSlot timeSlot;

    public static Application create(
            String name, Gender gender, String email, String phoneNumber, String university, String major,
            AcademicStatus academicStatus, LocalDate birthDate, String address, Recruitment recruitment, Position position
    ) {
        return Application.builder()
                .name(name)
                .gender(gender)
                .email(email)
                .phoneNumber(phoneNumber)
                .university(university)
                .major(major)
                .academicStatus(academicStatus)
                .birthDate(birthDate)
                .address(address)
                .status(ApplicationStatus.PENDING)
                .recruitment(recruitment)
                .position(position)
                .build();
    }

    public void updateIsMailSent(Boolean isMailSent) {
        this.isMailSent = isMailSent;
    }

    public void updateIsSmsSent(Boolean isSmsSent) {
        this.isSmsSent = isSmsSent;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.associateApplication(this);
    }

    public void associateInterview(Interview interview) {
        this.interview = interview;
    }

    public void addInterviewQuestion(InterviewQuestion question) {
        this.interviewQuestions.add(question);
        question.associateApplication(this);
    }

    public void addEvaluation(Evaluation evaluation) {
        this.evaluations.add(evaluation);
        evaluation.associateApplication(this);
    }

    public void assignTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }
}
