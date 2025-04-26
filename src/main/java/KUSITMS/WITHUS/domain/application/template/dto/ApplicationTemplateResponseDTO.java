package KUSITMS.WITHUS.domain.application.template.dto;

import KUSITMS.WITHUS.domain.application.template.entity.ApplicationTemplate;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "지원서 양식 관련 응답 DTO")
public class ApplicationTemplateResponseDTO {

    @Schema(description = "지원서 양식 상세 조회 응답 DTO")
    public record Detail(
            @Schema(description = "양식 ID") Long id,
            @Schema(description = "양식 제목") String title,
            @Schema(description = "서류 평가기준") List<String> documentEvaluationCriteria,
            @Schema(description = "면접 평가기준") List<String> interviewEvaluationCriteria
        ) {
        public static Detail from(ApplicationTemplate template) {
            List<EvaluationCriteria> criteriaList = template.getRecruitment().getEvaluationCriteriaList();

            List<String> documentCriteria = criteriaList.stream()
                    .filter(c -> c.getEvaluationType() == EvaluationType.DOCUMENT)
                    .map(EvaluationCriteria::getContent)
                    .toList();

            List<String> interviewCriteria = criteriaList.stream()
                    .filter(c -> c.getEvaluationType() == EvaluationType.INTERVIEW)
                    .map(EvaluationCriteria::getContent)
                    .toList();

            return new Detail(
                    template.getId(),
                    template.getTitle(),
                    documentCriteria,
                    interviewCriteria
            );
        }
    }
}
