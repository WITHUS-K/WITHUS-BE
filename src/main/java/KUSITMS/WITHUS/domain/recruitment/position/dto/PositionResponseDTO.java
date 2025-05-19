package KUSITMS.WITHUS.domain.recruitment.position.dto;

import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파트 관련 응답 DTO")
public class PositionResponseDTO {

    @Schema(description = "파트 상세 조회 응답 DTO")
    public record Detail(
            @Schema(description = "파트 ID") Long id,
            @Schema(description = "파트 이름") String name,
            @Schema(description = "색상") String color
    ) {
        public static Detail from(Position position) {
            return new Detail(
                    position.getId(),
                    position.getName(),
                    position.getColor()
            );
        }
    }

    @Schema(description = "포지션 지원자 수 정보 DTO")
    public record SummaryForRecruitment(
            @Schema(description = "포지션 이름") String name,
            @Schema(description = "지원자 수") int applicantCount
    ) {
        public static SummaryForRecruitment from(Position position) {
            return new SummaryForRecruitment(
                    position.getName(),
                    position.getApplications().size()
            );
        }
    }
}
