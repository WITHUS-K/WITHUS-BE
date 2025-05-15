package KUSITMS.WITHUS.domain.application.applicationEvaluator.repository;

import KUSITMS.WITHUS.domain.application.applicationEvaluator.entity.ApplicationEvaluator;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationEvaluatorJpaRepository extends JpaRepository<ApplicationEvaluator, Long> {
    void deleteAllByApplication_Recruitment_Id(Long recruitmentId);
    void deleteAllByApplication_Id(Long applicationId);
    List<ApplicationEvaluator> findByApplication_Recruitment_IdAndApplication_Position_IdAndEvaluationType(Long recruitmentId, Long positionId, EvaluationType evaluationType);
}
