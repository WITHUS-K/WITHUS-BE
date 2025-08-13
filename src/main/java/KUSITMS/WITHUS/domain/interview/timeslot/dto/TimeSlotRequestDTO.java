package KUSITMS.WITHUS.domain.interview.timeslot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "TimeSlot 관련 요청 DTO")
public class TimeSlotRequestDTO {

    @Schema(description = "TimeSlot에 지원자 추가 요청 DTO")
    public record UpsertApplicant(
            @Schema(description = "추가할 지원자 ID 목록", example = "[1, 2]") List<Long> applicantIds
    ) {}
}

