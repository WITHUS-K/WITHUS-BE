package KUSITMS.WITHUS.domain.interview.interview.dto;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "면접 타임슬롯 및 배정된 지원자 정보")
public record InterviewScheduleDTO(
        @Schema(description = "면접 일자") LocalDate date,
        @Schema(description = "시작 시간") LocalTime startTime,
        @Schema(description = "종료 시간") LocalTime endTime,
        @Schema(description = "지원자 목록") List<ApplicantInfo> applicants
) {

    public static InterviewScheduleDTO from(TimeSlot slot) {
        return new InterviewScheduleDTO(
                slot.getDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getApplications().stream()
                        .map(ApplicantInfo::from)
                        .collect(Collectors.toList())
        );
    }

    @Schema(description = "면접 배정 지원자 정보")
    public record ApplicantInfo(
            @Schema(description = "지원서 ID") Long applicationId,
            @Schema(description = "이름") String name,
            @Schema(description = "이메일") String email,
            @Schema(description = "파트") String positionName
    ) {
        public static ApplicantInfo from(Application app) {
            return new ApplicantInfo(
                    app.getId(),
                    app.getName(),
                    app.getEmail(),
                    app.getPosition().getName()
            );
        }
    }
}

