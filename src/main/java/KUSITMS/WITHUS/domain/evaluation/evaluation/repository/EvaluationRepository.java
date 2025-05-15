package KUSITMS.WITHUS.domain.evaluation.evaluation.repository;

import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;

import java.util.List;

public interface EvaluationRepository {
    Evaluation save(Evaluation evaluation);
    boolean existsByApplicationAndCriteriaAndUser(Long applicationId, Long criteriaId, Long userId);
    List<Evaluation> findEvaluationsForApplication(Long applicationId);
    long countFullyEvaluatedApplications(Long recruitmentId, Long positionId, EvaluationType stage, long requiredCriteriaCount);
}
