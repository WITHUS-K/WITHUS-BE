package KUSITMS.WITHUS.domain.interview.timeslotUser.dto;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "타임슬롯 사용자 응답 DTO")
public record TimeSlotUserResponseDTO(
        @Schema(description = "사용자 ID") Long userId,
        @Schema(description = "이름") String name,
        @Schema(description = "역할")InterviewRole role
        ) {
    public static TimeSlotUserResponseDTO from(TimeSlotUser timeSlotUser) {
        return new TimeSlotUserResponseDTO(
                timeSlotUser.getUser().getId(),
                timeSlotUser.getUser().getName(),
                timeSlotUser.getRole()
        );
    }
}

