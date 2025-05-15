package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.repository;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationCriteriaJpaRepository extends JpaRepository<EvaluationCriteria, Long> {
    List<EvaluationCriteria> findByEvaluationTypeAndRecruitmentId(EvaluationType evaluationType, Long recruitmentId);
    Long countByRecruitment_IdAndEvaluationType(Long recruitmentId, EvaluationType stage);
    List<EvaluationCriteria> findByRecruitment_IdAndEvaluationType(Long recruitmentId, EvaluationType stage);
}
