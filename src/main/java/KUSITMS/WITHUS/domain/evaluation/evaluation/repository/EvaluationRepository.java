package KUSITMS.WITHUS.domain.evaluation.evaluation.repository;

import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;

import java.util.List;

public interface EvaluationRepository {
    Evaluation save(Evaluation evaluation);
    boolean existsByApplicationAndCriteriaAndUser(Long applicationId, Long criteriaId, Long userId);
    List<Evaluation> findAllByApplicationIdWithCriteriaAndUser(Long applicationId);
}
