package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "평가 기준 관련 요청 DTO")
public class EvaluationCriteriaRequestDTO {

    @Schema(description = "평가 기준 추가 요청 DTO")
    public record Create(
            @Schema(description = "평가 기준", example = "성실성")
            @NotBlank String content,

            @Schema(description = "상세 설명", example = "열심히 참여할 것 같은지")
            @NotBlank String description,

            @Schema(description = "평가 타입", example = "INTERVIEW")
            @NotNull EvaluationType type
    ) {}
}
