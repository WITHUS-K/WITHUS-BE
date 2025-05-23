package KUSITMS.WITHUS.domain.application.applicationEvaluator.repository;

import KUSITMS.WITHUS.domain.application.applicationEvaluator.entity.ApplicationEvaluator;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;

import java.util.List;

public interface ApplicationEvaluatorRepository {
    void deleteAllByApplication_Recruitment_Id(Long recruitmentId);
    void deleteAllByApplication_Id(Long applicationId);
    List<ApplicationEvaluator> findByRecruitmentAndPositionAndType(Long recruitmentId, Long positionId, EvaluationType type);
    List<ApplicationEvaluator> findByEvaluatorAndRecruitmentAndType(Long evaluatorId, EvaluationType type, Long recruitmentId);
    void saveAll(List<ApplicationEvaluator> assigns);
}
