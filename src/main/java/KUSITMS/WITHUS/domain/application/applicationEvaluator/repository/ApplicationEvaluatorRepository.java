package KUSITMS.WITHUS.domain.application.applicationEvaluator.repository;

import KUSITMS.WITHUS.domain.application.applicationEvaluator.entity.ApplicationEvaluator;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface ApplicationEvaluatorRepository {
    void deleteAllByApplication_Recruitment_IdAndEvaluationType(Long recruitmentId, EvaluationType evaluationType);
    void deleteAllByApplication_Id(Long applicationId);
    List<ApplicationEvaluator> findByRecruitmentAndOrganizationRoleAndType(Long recruitmentId, Long organizationRoleId, EvaluationType type);
    List<ApplicationEvaluator> findByEvaluatorAndRecruitmentAndType(Long evaluatorId, EvaluationType type, Long recruitmentId);
    void saveAll(List<ApplicationEvaluator> assigns);
    void deleteAllByApplication_IdAndEvaluationType(Long id, EvaluationType evaluationType);
}
