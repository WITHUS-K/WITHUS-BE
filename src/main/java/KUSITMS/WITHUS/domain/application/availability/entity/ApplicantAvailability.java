package KUSITMS.WITHUS.domain.application.availability.entity;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "APPLICANT_AVAILABILITY")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicantAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPLICANT_AVAILABILITY__ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICATION_ID", nullable = false)
    private Application application;

    @Column(name = "AVAILABLE_TIME", nullable = false)
    private LocalDateTime availableTime;

    public static ApplicantAvailability of(Application application, LocalDateTime time) {
        return ApplicantAvailability.builder()
                .application(application)
                .availableTime(time)
                .build();
    }
}
