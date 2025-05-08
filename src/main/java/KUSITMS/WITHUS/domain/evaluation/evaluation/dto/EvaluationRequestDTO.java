package KUSITMS.WITHUS.domain.evaluation.evaluation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "평가 관련 요청 DTO")
public class EvaluationRequestDTO {

    @Schema(name = "EvaluationRequestDTO.Create", description = "평가 요청 DTO")
    public record Create(
            @Schema(description = "지원서 ID", example = "1")
            @NotNull Long applicationId,

            @Schema(description = "평가 기준 ID", example = "1")
            @NotNull Long criteriaId,

            @Schema(description = "점수", example = "5")
            @NotNull int score
    ) {}

    @Schema(name = "EvaluationRequestDTO.BulkCreate", description = "다중 평가 요청 DTO")
    public record BulkCreate(
            @Schema(description = "지원서 ID", example = "1")
            @NotNull Long applicationId,

            @Schema(description = "평가 항목 리스트", required = true)
            @NotEmpty List<EvaluationItem> evaluations

    ) {
        @Schema(name = "EvaluationRequestDTO.BulkCreate.EvaluationItem", description = "평가 항목 DTO")
        public record EvaluationItem(
                @Schema(description = "평가 기준 ID", example = "3")
                @NotNull Long criteriaId,

                @Schema(description = "평가 점수", example = "5")
                @NotNull int score

        ) {}
    }
}
