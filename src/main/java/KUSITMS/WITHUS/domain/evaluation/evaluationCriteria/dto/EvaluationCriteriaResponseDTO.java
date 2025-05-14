package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

@Schema(description = "평가 기준 관련 응답 DTO")
public class EvaluationCriteriaResponseDTO {

    @Schema(description = "평가 기준 추가 응답 DTO")
    public record Create(
            @Schema(description = "평가 기준") String content,
            @Schema(description = "평가 타입") EvaluationType type
    ) {
        public static Create from(EvaluationCriteria evaluationCriteria) {
            return new Create(
                    evaluationCriteria.getContent(),
                    evaluationCriteria.getEvaluationType()
            );
        }
    }

    @Schema(description = "평가 기준 정보 응답 DTO")
    public record Detail(
            @Schema(description = "평가 기준 ID") Long id,
            @Schema(description = "평가 기준") String content,
            @Schema(description = "상세 내용") String description,
            @Schema(description = "평가 타입") EvaluationType type,
            @Schema(description = "평가 점수 - 평가한 게 없다면 null") @Nullable Integer score
    ) {
        public static Detail from(EvaluationCriteria evaluationCriteria) {
            return new Detail(
                    evaluationCriteria.getId(),
                    evaluationCriteria.getContent(),
                    evaluationCriteria.getDescription(),
                    evaluationCriteria.getEvaluationType(),
                    null
            );
        }

        public static Detail from(EvaluationCriteria evaluationCriteria, @Nullable Integer score) {
            return new Detail(
                    evaluationCriteria.getId(),
                    evaluationCriteria.getContent(),
                    evaluationCriteria.getDescription(),
                    evaluationCriteria.getEvaluationType(),
                    score
            );
        }
    }

    @Schema(description = "평가 기준 요약 정보 응답 DTO")
    public record Summary(
            @Schema(description = "평가 기준 ID") Long id,
            @Schema(description = "평가 기준") String content,
            @Schema(description = "평가 타입") EvaluationType type
    ) {
        public static Summary from(EvaluationCriteria evaluationCriteria) {
            return new Summary(
                    evaluationCriteria.getId(),
                    evaluationCriteria.getContent(),
                    evaluationCriteria.getEvaluationType()
            );
        }
    }
}
