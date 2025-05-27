package KUSITMS.WITHUS.domain.evaluation.evaluation.repository;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.user.user.entity.User;

import java.util.List;

public interface EvaluationRepository {
    Evaluation save(Evaluation evaluation);
    void saveAll(List<Evaluation> evaluations);
    boolean existsByApplicationAndCriteriaAndUser(Long applicationId, Long criteriaId, Long userId);
    List<Evaluation> findEvaluationsForApplication(Long applicationId);
    List<Evaluation> findByApplicationAndUserAndCriteriaIn(Application application, User user, List<EvaluationCriteria> criterias);
    long countFullyEvaluatedApplications(Long recruitmentId, Long positionId, EvaluationType stage, long requiredCriteriaCount);
    long countByApplication_IdAndUser_IdAndCriteria_IdIn(Long applicationId, Long userId, List<Long> criteriaIds);
    long countByApplication_IdAndUser_IdAndCriteria_EvaluationType(Long id, Long userId, EvaluationType evaluationType);
    void deleteAll(List<Evaluation> existingEvaluations);
}
