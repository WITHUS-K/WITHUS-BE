package KUSITMS.WITHUS.domain.evaluation.evaluation.repository;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationJpaRepository extends JpaRepository<Evaluation, Long> {
    boolean existsByApplicationIdAndCriteriaIdAndUserId(Long applicationId, Long criteriaId, Long userId);
    List<Evaluation> findByApplicationAndUserAndCriteriaIn(Application application, User user, List<EvaluationCriteria> criterias);
    long countByApplication_IdAndUser_IdAndCriteria_IdIn(Long applicationId, Long userId, List<Long> criteriaIds);
    long countByApplication_IdAndUser_IdAndCriteria_EvaluationType(Long id, Long userId, EvaluationType evaluationType);
}

