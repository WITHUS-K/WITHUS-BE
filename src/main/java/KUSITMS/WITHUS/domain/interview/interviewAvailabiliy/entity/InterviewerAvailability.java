package KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity;

import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "INTERVIEWER_AVAILABILITY")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InterviewerAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INTERVIEWER_AVAILABILITY_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTERVIEW_ID", nullable = false)
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "AVAILABLE_TIME", nullable = false)
    private LocalDateTime availableTime;

    public static InterviewerAvailability of(Interview interview, User user, LocalDateTime time) {
        return InterviewerAvailability.builder()
                .interview(interview)
                .user(user)
                .availableTime(time)
                .build();
    }

    public void associateInterview(Interview interview) {
        this.interview = interview;
    }
}

