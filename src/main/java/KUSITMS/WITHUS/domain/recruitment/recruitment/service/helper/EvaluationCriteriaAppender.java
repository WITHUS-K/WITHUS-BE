package KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.position.repository.PositionRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EvaluationCriteriaAppender {

    private final PositionRepository positionRepository;

    public void appendWithPositions(
            Recruitment recruitment,
            List<EvaluationCriteriaRequestDTO.Create> criteriaList,
            EvaluationType type
    ) {
        if (criteriaList == null) return;
        recruitment.clearEvaluationCriteria();

        for (EvaluationCriteriaRequestDTO.Create dto : criteriaList) {
            Position position = getPositionIfExists(dto.positionId());

            EvaluationCriteria criteria = EvaluationCriteria.builder()
                    .content(dto.content())
                    .evaluationType(type)
                    .position(position)
                    .build();

            recruitment.addEvaluationCriteria(criteria);
        }
    }

    private Position getPositionIfExists(Long positionId) {
        if (positionId == null) return null;
        return positionRepository.getById(positionId);
    }
}
