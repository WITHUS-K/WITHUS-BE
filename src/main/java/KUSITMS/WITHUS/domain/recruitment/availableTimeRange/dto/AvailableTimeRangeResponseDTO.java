package KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto;

import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.entity.AvailableTimeRange;
import KUSITMS.WITHUS.global.common.annotation.DateFormatDot;
import KUSITMS.WITHUS.global.common.annotation.TimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "면접 일정 시간 추가 요청 DTO")
public record AvailableTimeRangeResponseDTO(
        @Schema(description = "면접일정 시간 ID") Long id,
        @Schema(description = "날짜") @DateFormatDot LocalDate date,
        @Schema(description = "시작 시간") @TimeFormat LocalTime startTime,
        @Schema(description = "종료 시간") @TimeFormat LocalTime endTime,
        @Schema(description = "공고 ID") Long recruitmentId
) {
    public static AvailableTimeRangeResponseDTO from(AvailableTimeRange availableTimeRange) {
        return new AvailableTimeRangeResponseDTO(
                availableTimeRange.getId(),
                availableTimeRange.getDate(),
                availableTimeRange.getStartTime(),
                availableTimeRange.getEndTime(),
                availableTimeRange.getRecruitment().getId()
        );
    }
}