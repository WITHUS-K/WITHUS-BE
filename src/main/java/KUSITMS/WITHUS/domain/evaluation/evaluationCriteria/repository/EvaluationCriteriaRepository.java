package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.repository;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;

public interface EvaluationCriteriaRepository {
    EvaluationCriteria getById(Long id);
}
