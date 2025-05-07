package KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EvaluationCriteriaAppender {

    public void appendWithPositions(
            Recruitment recruitment,
            List<EvaluationCriteriaRequestDTO.Create> criteriaList,
            EvaluationType type
    ) {
        if (criteriaList == null) return;

        for (EvaluationCriteriaRequestDTO.Create dto : criteriaList) {

            EvaluationCriteria criteria = EvaluationCriteria.builder()
                    .content(dto.content())
                    .description(dto.description())
                    .evaluationType(type)
                    .build();

            recruitment.addEvaluationCriteria(criteria);
        }
    }
}
