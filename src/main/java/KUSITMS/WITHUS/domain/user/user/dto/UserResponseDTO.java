package KUSITMS.WITHUS.domain.user.user.dto;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 관련 응답 DTO")
public class UserResponseDTO {

    @Schema(description = "사용자 요약 정보 응답 DTO")
    public record Summary(
            @Schema(description = "사용자 ID") Long userId,
            @Schema(description = "이름") String name
    ) {
        public static Summary from(User user) {
            return new Summary(
                    user.getId(),
                    user.getName()
            );
        }
    }

    @Schema(description = "TimeSlot에서 사용할 사용자 요약 정보 응답 DTO")
    public record SummaryForTimeSlot(
            @Schema(description = "사용자 ID") Long userId,
            @Schema(description = "이름") String name,
            @Schema(description = "역할") InterviewRole role
    ) {
        public static SummaryForTimeSlot from(User user, InterviewRole role) {
            return new SummaryForTimeSlot(
                    user.getId(),
                    user.getName(),
                    role
            );
        }
    }
}
