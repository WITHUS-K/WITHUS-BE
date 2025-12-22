package KUSITMS.WITHUS.domain.application.application.repository;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationJpaRepository extends JpaRepository<Application, Long> {
    List<Application> findByRecruitmentId(Long recruitmentId);
    List<Application> findByRecruitmentIdAndStatusIn(Long recruitmentId, List<ApplicationStatus> statuses);
    List<Application> findByRecruitment_IdAndOrganizationRole_Id(Long recruitmentId, Long organizationRoleId);
    List<Application> findByRecruitment_IdAndOrganizationRoleIsNull(Long recruitmentId);
    Long countByRecruitment_IdAndOrganizationRole_Id(Long recruitmentId, Long organizationRoleId);
    List<Application> findDistinctByRecruitment_IdAndEvaluators_Evaluator_IdAndEvaluators_EvaluationType(Long recruitmentId, Long evaluatorId, EvaluationType evaluationType);
    Long countByRecruitmentIdAndStatusIn(Long recruitmentId, List<ApplicationStatus> statuses);
}
