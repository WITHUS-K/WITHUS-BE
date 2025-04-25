package KUSITMS.WITHUS.domain.evaluation.evaluation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "평가 관련 요청 DTO")
public class EvaluationRequestDTO {

    @Schema(description = "평가 요청 DTO")
    public record Create(
            @Schema(description = "지원서 ID", example = "1")
            @NotNull Long applicationId,

            @Schema(description = "평가 기준 ID", example = "1")
            @NotNull Long criteriaId,

            @Schema(description = "점수", example = "5")
            @NotNull int score
    ) {}
}
