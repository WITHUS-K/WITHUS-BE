package KUSITMS.WITHUS.domain.evaluation.evaluation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

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

    @Schema(description = "다중 평가 요청 DTO")
    public record BulkCreate(
            @Schema(description = "지원서 ID", example = "1")
            @NotNull Long applicationId,

            @Schema(description = "평가 항목 리스트")
            @NotEmpty List<EvaluationItem> evaluations

    ) {
        @Schema(description = "평가 항목 DTO")
        public record EvaluationItem(
                @Schema(description = "평가 기준 ID", example = "3")
                @NotNull Long criteriaId,

                @Schema(description = "평가 점수", example = "5")
                @NotNull int score

        ) {}
    }

    @Schema(description = "평가 미완료자 리마인드 메일 전송 요청 DTO")
    public record Reminder(
            @Schema(description = "미완료 사용자 ID 리스트", example = "[1, 2, 3]")
            @NotEmpty List<Long> userIds
    ) {}
}
