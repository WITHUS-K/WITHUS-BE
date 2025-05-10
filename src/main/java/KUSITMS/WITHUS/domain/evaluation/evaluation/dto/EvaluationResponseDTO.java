package KUSITMS.WITHUS.domain.evaluation.evaluation.dto;

import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "평가 관련 응답 DTO")
public class EvaluationResponseDTO {

    @Schema(description = "평가 정보 응답 DTO")
    public record Detail(
            @Schema(description = "평가 ID") Long id,
            @Schema(description = "평가 기준") String criteria,
            @Schema(description = "평가 점수") int score,
            @Schema(description = "평가자") UserResponseDTO.Summary user
    ) {
        public static Detail from(Evaluation evaluation) {
            return new Detail(
                    evaluation.getId(),
                    evaluation.getCriteria().getContent(),
                    evaluation.getScore(),
                    UserResponseDTO.Summary.from(evaluation.getUser())
            );
        }
    }
}
