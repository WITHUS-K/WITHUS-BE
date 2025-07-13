package KUSITMS.WITHUS.domain.application.applicationEvaluator.repository;

import KUSITMS.WITHUS.domain.application.applicationEvaluator.entity.ApplicationEvaluator;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationEvaluatorJpaRepository extends JpaRepository<ApplicationEvaluator, Long> {
    void deleteAllByApplication_Recruitment_IdAndEvaluationType(Long recruitmentId, EvaluationType evaluationType);
    void deleteAllByApplication_Id(Long applicationId);
    void deleteAllByApplication_IdAndEvaluationType(Long applicationId, EvaluationType evaluationType);
}
