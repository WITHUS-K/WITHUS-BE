package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "평가 기준 관련 요청 DTO")
public class EvaluationCriteriaRequestDTO {

    @Schema(description = "평가 기준 추가 요청 DTO")
    public record Create(
            @Schema(description = "평가 기준", example = "열심히 참여할 것 같은지")
            @NotBlank String content,

            @Schema(description = "평가 타입", example = "INTERVIEW")
            @NotNull EvaluationType type,

            @Schema(description = "적용할 파트 ID - null이면 공통", example = "1")
            Long positionId
    ) {}
}
