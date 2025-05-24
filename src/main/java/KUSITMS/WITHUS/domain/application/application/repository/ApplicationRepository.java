package KUSITMS.WITHUS.domain.application.application.repository;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;

import java.util.List;

public interface ApplicationRepository {
    Application getById(Long id);
    Application save(Application application);
    void delete(Long id);
    List<Application> findPassedByRecruitment(Long recruitmentId);
    List<Application> findByRecruitmentId(Long recruitmentId);
    List<Application> findByRecruitmentIdAndStatusIn(Long recruitmentId, List<ApplicationStatus> statuses);
    List<Application> findByRecruitment_IdAndPosition_Id(Long recruitmentId, Long positionId);
    Long countByRecruitment_IdAndPosition_Id(Long recruitmentId, Long positionId);
    List<Application> findAllById(List<Long> longs);
    List<Application> findDistinctByRecruitment_IdAndEvaluators_Evaluator_IdAndEvaluators_EvaluationType(Long recruitmentId, Long evaluatorId, EvaluationType evaluationType);
}
