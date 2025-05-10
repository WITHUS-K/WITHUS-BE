package KUSITMS.WITHUS.domain.interview.interview.dto;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.global.common.annotation.DateFormatDot;
import KUSITMS.WITHUS.global.common.annotation.TimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "면접 타임슬롯 및 배정된 지원자 정보")
public record InterviewScheduleDTO(
        @Schema(description = "면접 일자") @DateFormatDot LocalDate date,
        @Schema(description = "시작 시간") @TimeFormat LocalTime startTime,
        @Schema(description = "종료 시간") @TimeFormat LocalTime endTime,
        @Schema(description = "지원자 목록") List<ApplicantInfo> applicants,
        @Schema(description = "면접관 목록") List<UserResponseDTO.SummaryForTimeSlot> interviewers,
        @Schema(description = "안내자 목록") List<UserResponseDTO.SummaryForTimeSlot> assistants
) {

    public static InterviewScheduleDTO from(TimeSlot slot) {
        List<TimeSlotUser> users = slot.getTimeSlotUsers();

        List<UserResponseDTO.SummaryForTimeSlot> interviewers = users.stream()
                .filter(u -> u.getRole() == InterviewRole.INTERVIEWER)
                .map(u -> UserResponseDTO.SummaryForTimeSlot.from(u.getUser(), u.getRole()))
                .toList();

        List<UserResponseDTO.SummaryForTimeSlot> assistants = users.stream()
                .filter(u -> u.getRole() == InterviewRole.ASSISTANT)
                .map(u -> UserResponseDTO.SummaryForTimeSlot.from(u.getUser(), u.getRole()))
                .toList();

        return new InterviewScheduleDTO(
                slot.getDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getApplications().stream()
                        .map(ApplicantInfo::from)
                        .collect(Collectors.toList()),
                interviewers,
                assistants
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
            String positionName = app.getPosition() != null ? app.getPosition().getName() : null;
            return new ApplicantInfo(
                    app.getId(),
                    app.getName(),
                    app.getEmail(),
                    positionName
            );
        }
    }

    @Schema(description = "내 면접 시간 응답 DTO")
    public record MyInterviewTimeDTO(
            @Schema(description = "면접 ID") Long interviewId,
            @Schema(description = "면접 날짜") @DateFormatDot LocalDate date,
            @Schema(description = "시작 시간") @TimeFormat LocalTime startTime,
            @Schema(description = "종료 시간") @TimeFormat LocalTime endTime
    ) {
        public static MyInterviewTimeDTO from(TimeSlot timeSlot) {
            return new MyInterviewTimeDTO(
                    timeSlot.getInterview().getId(),
                    timeSlot.getDate(),
                    timeSlot.getStartTime(),
                    timeSlot.getEndTime()
            );
        }
    }
}

