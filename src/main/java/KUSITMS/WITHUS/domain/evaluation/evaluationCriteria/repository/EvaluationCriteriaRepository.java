package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.repository;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;

import java.util.List;

public interface EvaluationCriteriaRepository {
    EvaluationCriteria getById(Long id);
    List<EvaluationCriteria> findByTypeAndRecruitment(EvaluationType type, Long recruitmentId);
}
