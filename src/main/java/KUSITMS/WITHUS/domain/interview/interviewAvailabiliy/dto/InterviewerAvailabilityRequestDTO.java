package KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "면접 가능한 시간 요청 DTO")
public record InterviewerAvailabilityRequestDTO(
        @Schema(description = "가능한 시간 리스트", example = "[\"2025-05-22T10:00:00\", \"2025-05-22T11:00:00\"]")
        @NotEmpty List<LocalDateTime> availableTimes
) {}
