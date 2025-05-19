package KUSITMS.WITHUS.domain.interview.interview.dto;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeResponseDTO;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.global.common.annotation.DateFormatDot;
import KUSITMS.WITHUS.global.common.annotation.TimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "면접 일자별 스케줄 요약")
public record InterviewScheduleDTO(
        @Schema(description = "면접 ID") Long interviewId,
        @Schema(description = "면접 일자") @DateFormatDot LocalDate date,
        @Schema(description = "시작 시간") @TimeFormat LocalTime startTime,
        @Schema(description = "종료 시간") @TimeFormat LocalTime endTime,
        @Schema(description = "면접 소요 시간") Short interviewDuration,
        @Schema(description = "면접실 이름 목록") List<String> roomNames,
        @Schema(description = "해당 날짜의 타임슬롯 목록") List<InterviewSlotDTO> timeSlots
) {
    public static InterviewScheduleDTO from(
            Long interviewId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            Short interviewDuration,
            List<String> roomNames,
            List<InterviewSlotDTO> timeSlots
    ) {
        return new InterviewScheduleDTO(interviewId, date, startTime, endTime, interviewDuration, roomNames, timeSlots);
    }

    @Schema(description = "면접 타임슬롯 및 배정된 지원자 정보")
    public record InterviewSlotDTO(
            @Schema(description = "타임슬롯 ID") Long timeSlotId,
            @Schema(description = "면접 ID") Long interviewId,
            @Schema(description = "면접 일자") @DateFormatDot LocalDate date,
            @Schema(description = "면접실 이름") String roomName,
            @Schema(description = "시작 시간") @TimeFormat LocalTime startTime,
            @Schema(description = "종료 시간") @TimeFormat LocalTime endTime,
            @Schema(description = "지원자 목록") List<ApplicantInfo> applicants,
            @Schema(description = "면접관 목록") List<UserResponseDTO.SummaryForTimeSlot> interviewers,
            @Schema(description = "안내자 목록") List<UserResponseDTO.SummaryForTimeSlot> assistants
    ) {
        public static InterviewSlotDTO from(TimeSlot slot) {
            List<TimeSlotUser> users = slot.getTimeSlotUsers();

            List<UserResponseDTO.SummaryForTimeSlot> interviewers = users.stream()
                    .filter(u -> u.getRole() == InterviewRole.INTERVIEWER)
                    .map(u -> UserResponseDTO.SummaryForTimeSlot.from(u.getUser(), u.getRole()))
                    .toList();

            List<UserResponseDTO.SummaryForTimeSlot> assistants = users.stream()
                    .filter(u -> u.getRole() == InterviewRole.ASSISTANT)
                    .map(u -> UserResponseDTO.SummaryForTimeSlot.from(u.getUser(), u.getRole()))
                    .toList();

            return new InterviewSlotDTO(
                    slot.getId(),
                    slot.getInterview().getId(),
                    slot.getDate(),
                    slot.getRoomName(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    slot.getApplications().stream().map(ApplicantInfo::from).toList(),
                    interviewers,
                    assistants
            );
        }

        public static InterviewSlotDTO from(TimeSlot slot, boolean isInvolved) {
            List<UserResponseDTO.SummaryForTimeSlot> interviewers = List.of();
            List<UserResponseDTO.SummaryForTimeSlot> assistants = List.of();
            List<ApplicantInfo> applicants = List.of();

            if (isInvolved) {
                List<TimeSlotUser> users = slot.getTimeSlotUsers();

                interviewers = users.stream()
                        .filter(u -> u.getRole() == InterviewRole.INTERVIEWER)
                        .map(u -> UserResponseDTO.SummaryForTimeSlot.from(u.getUser(), u.getRole()))
                        .toList();

                assistants = users.stream()
                        .filter(u -> u.getRole() == InterviewRole.ASSISTANT)
                        .map(u -> UserResponseDTO.SummaryForTimeSlot.from(u.getUser(), u.getRole()))
                        .toList();

                applicants = slot.getApplications().stream()
                        .map(ApplicantInfo::from)
                        .toList();
            }

            return new InterviewSlotDTO(
                    slot.getId(),
                    slot.getInterview().getId(),
                    slot.getDate(),
                    slot.getRoomName(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    applicants,
                    interviewers,
                    assistants
            );
        }

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

    public record MyInterviewScheduleSummaryDTO(
            Long recruitmentId,
            String recruitmentTitle,
            Long interviewId,
            List<AvailableTimeRangeResponseDTO> availableTimeRanges,
            Short interviewDuration
    ) {}
}
