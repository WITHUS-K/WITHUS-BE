package KUSITMS.WITHUS.domain.interview.timeslotUser.dto;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "TimeSlot 관련 요청 DTO")
public class TimeSlotUserRequestDTO {

    @Schema(description = "TimeSlot에 사용자 추가 요청 DTO")
    public record AddUser(
            @Schema(description = "추가할 사용자 ID 목록", example = "[1, 2]") List<Long> userIds,
            @Schema(description = "면접 역할", example = "INTERVIEWER") InterviewRole role
    ) {}
}
