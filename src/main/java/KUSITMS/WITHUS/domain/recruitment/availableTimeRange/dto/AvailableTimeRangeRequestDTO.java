package KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "면접 가능 시간 추가 요청 DTO")
public record AvailableTimeRangeRequestDTO(
        @Schema(description = "날짜", example = "2025-07-30")
        @NotNull LocalDate date,

        @Schema(description = "시작 시간", example = "10:00")
        @NotNull LocalTime startTime,

        @Schema(description = "종료 시간", example = "12:00")
        @NotNull LocalTime endTime
) {}
