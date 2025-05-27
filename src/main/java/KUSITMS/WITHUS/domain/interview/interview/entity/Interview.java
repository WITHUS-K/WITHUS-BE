package KUSITMS.WITHUS.domain.interview.interview.entity;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity.InterviewerAvailability;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "INTERVIEW")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Interview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INTERVIEW_ID")
    private Long id;

    @Column(name = "INTERVIEWER_PER_SLOT")
    private Integer interviewerPerSlot;

    @Column(name = "APPLICANT_PER_SLOT")
    private Integer applicantPerSlot;

    @Column(name = "ASSISTANT_PER_SLOT")
    private Integer assistantPerSlot;

    @Column(name = "ROOM_COUNT", nullable = false)
    private int roomCount;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "INTERVIEW_ROOM_NAMES", joinColumns = @JoinColumn(name = "interview_id"))
    @Column(name = "ROOM_NAME")
    private List<String> roomNames = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUITMENT_ID", nullable = false)
    private Recruitment recruitment;

    @Builder.Default
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlot> timeSlots = new ArrayList<>();

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InterviewerAvailability> interviewerAvailabilities = new ArrayList<>();

    public void addInterviewerAvailability(InterviewerAvailability availability) {
        this.interviewerAvailabilities.add(availability);
        availability.associateInterview(this);
    }

    public void setConfig(int interviewerPerSlot, int applicantPerSlot, int assistantPerSlot, int roomCount) {
        this.interviewerPerSlot = interviewerPerSlot;
        this.applicantPerSlot = applicantPerSlot;
        this.assistantPerSlot = assistantPerSlot;
        this.roomCount = roomCount;
    }

    public void setRoomNames(List<String> names) {
        this.roomNames.clear();
        this.roomNames.addAll(names);
    }
}
