package KUSITMS.WITHUS.domain.interview.timeslot.dto;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewResponseDTO;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslotUser.dto.TimeSlotUserResponseDTO;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import KUSITMS.WITHUS.domain.organization.organizationRole.dto.OrganizationRoleResponseDTO;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.global.common.annotation.TimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;
import java.util.List;

@Schema(description = "타임슬롯 응답 DTO")
public class TimeSlotResponseDTO {
    public record Detail(
            @Schema(description = "타임슬롯 ID") Long timeSlotId,
            @Schema(description = "시작 시각") @TimeFormat LocalTime startAt,
            @Schema(description = "종료 시각") @TimeFormat LocalTime endAt,
            @Schema(description = "역할") OrganizationRoleResponseDTO.Detail organizationRole,

            @Schema(description = "배정된 사용자 목록") List<TimeSlotUserResponseDTO> users,
            @Schema(description = "배정된 지원자 목록") List<ApplicationResponseDTO.Applicant> applicants
    ) {
        public static TimeSlotResponseDTO.Detail from(
                TimeSlot slot,
                List<TimeSlotUser> users,
                List<ApplicationResponseDTO.Applicant> applicants
        ) {
            return new TimeSlotResponseDTO.Detail(
                    slot.getId(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    slot.getOrganizationRole() != null ? OrganizationRoleResponseDTO.Detail.from(slot.getOrganizationRole()) : null,
                    users.stream().map(TimeSlotUserResponseDTO::from).toList(),
                    applicants
            );
        }
    }

    public record ScheduleCard(
            Long timeSlotId,
            Long interviewId,
            String roomName,
            @TimeFormat LocalTime startTime,
            @TimeFormat LocalTime endTime,
            List<ApplicationResponseDTO.Applicant> applicants,
            List<UserResponseDTO.Summary> interviewers,
            List<UserResponseDTO.Summary> assistants
    ) {
        public static TimeSlotResponseDTO.ScheduleCard from(TimeSlot slot, List<ApplicationResponseDTO.Applicant> applicants, List<UserResponseDTO.Summary> interviewers, List<UserResponseDTO.Summary> assistants) {
            return new TimeSlotResponseDTO.ScheduleCard(
                    slot.getId(),
                    slot.getInterview() != null ? slot.getInterview().getId() : null,
                    slot.getRoomName(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    applicants,
                    interviewers,
                    assistants
            );
        }
    }
}
