package KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.dto;

import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity.InterviewerAvailability;
import KUSITMS.WITHUS.global.common.annotation.TimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "면접 가능한 시간 응답 DTO")
public record InterviewerAvailabilityResponseDTO (
        @Schema(description = "면접 가능 시간") @TimeFormat LocalDateTime availableTime
) {
    public static InterviewerAvailabilityResponseDTO from(InterviewerAvailability availability) {
        return new InterviewerAvailabilityResponseDTO(
                availability.getAvailableTime()
        );
    }
}
