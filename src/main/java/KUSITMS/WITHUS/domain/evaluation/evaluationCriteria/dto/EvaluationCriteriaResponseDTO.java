package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import io.swagger.v3.oas.annotations.media.Schema;

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
            @Schema(description = "평가 타입") EvaluationType type
    ) {
        public static Detail from(EvaluationCriteria evaluationCriteria) {
            return new Detail(
                    evaluationCriteria.getId(),
                    evaluationCriteria.getContent(),
                    evaluationCriteria.getDescription(),
                    evaluationCriteria.getEvaluationType()
            );
        }
    }
}
