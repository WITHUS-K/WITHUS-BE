package KUSITMS.WITHUS.domain.interview.timeslotUser.entity;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TIME_SLOT_USER")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeSlotUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TIME_SLOT_USER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIME_SLOT_ID", nullable = false)
    private TimeSlot timeSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private InterviewRole role;

    public void assignTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void assignUser(User user) {
        this.user = user;
    }
}
