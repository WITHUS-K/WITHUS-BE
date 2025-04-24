package KUSITMS.WITHUS.domain.evaluation.evaluation.repository;

import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationJpaRepository extends JpaRepository<Evaluation, Long> {
    boolean existsByApplicationIdAndCriteriaIdAndUserId(Long applicationId, Long criteriaId, Long userId);
}

