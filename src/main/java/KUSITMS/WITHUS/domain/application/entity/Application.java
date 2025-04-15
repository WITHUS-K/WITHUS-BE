package KUSITMS.WITHUS.domain.application.entity;

import KUSITMS.WITHUS.domain.application.enumrate.ApplicationStatus;
import KUSITMS.WITHUS.domain.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.entity.TimeSlot;
import KUSITMS.WITHUS.domain.user.entity.User;
import KUSITMS.WITHUS.global.common.BaseEntity;
import KUSITMS.WITHUS.global.common.enumrate.Gender;
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

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPLICATION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_ID", nullable = false)
    private ApplicationTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_ID", nullable = false)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTERVIEW_ID")
    private Interview interview;

    @Builder.Default
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIME_SLOT_ID")
    private TimeSlot timeSlot;

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

    public void assignTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }
}
