package KUSITMS.WITHUS.domain.interview.timeslot.dto;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslotUser.dto.TimeSlotUserResponseDTO;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import KUSITMS.WITHUS.domain.organization.organizationRole.dto.OrganizationRoleResponseDTO;
import KUSITMS.WITHUS.global.common.annotation.TimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;
import java.util.List;

@Schema(description = "타임슬롯 응답 DTO")
public record TimeSlotResponseDTO(
        @Schema(description = "타임슬롯 ID") Long timeSlotId,
        @Schema(description = "시작 시각") @TimeFormat LocalTime startAt,
        @Schema(description = "종료 시각") @TimeFormat LocalTime endAt,
        @Schema(description = "역할") OrganizationRoleResponseDTO.Detail organizationRole,

        @Schema(description = "배정된 사용자 목록") List<TimeSlotUserResponseDTO> users,
        @Schema(description = "배정된 지원자 목록") List<ApplicationResponseDTO.Applicant> applicants
) {
    public static TimeSlotResponseDTO from(
            TimeSlot slot,
            List<TimeSlotUser> users,
            List<ApplicationResponseDTO.Applicant> applicants
    ) {
        return new TimeSlotResponseDTO(
                slot.getId(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getOrganizationRole() != null ? OrganizationRoleResponseDTO.Detail.from(slot.getOrganizationRole()) : null,
                users.stream().map(TimeSlotUserResponseDTO::from).toList(),
                applicants
        );
    }
}
