package KUSITMS.WITHUS.domain.application.application.repository;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;

import java.util.List;

public interface ApplicationRepository {
    Application getById(Long id);
    Application save(Application application);
    void delete(Long id);
    List<Application> findPassedByRecruitment(Long recruitmentId);
    List<Application> findByRecruitmentIdAndStatusIn(Long recruitmentId, List<ApplicationStatus> statuses);
    List<Application> findByRecruitment_IdAndOrganizationRole_Id(Long recruitmentId, Long organizationRoleId);
    List<Application> findByRecruitment_IdAndOrganizationRoleIsNull(Long recruitmentId);
    Long countByRecruitment_IdAndOrganizationRole_Id(Long recruitmentId, Long organizationRoleId);
    List<Application> findAllById(List<Long> longs);
    List<Application> findDistinctByRecruitment_IdAndEvaluators_Evaluator_IdAndEvaluators_EvaluationType(Long recruitmentId, Long evaluatorId, EvaluationType evaluationType);
    Long countByRecruitmentIdAndStatusIn(Long recruitmentId, List<ApplicationStatus> statuses);
    List<ApplicationResponseDTO.CandidateDTO> findEligibleCandidates(Long recruitmentId, Long timeslotId, String q, boolean excludeCurrent);
    List<Application> findForTimeSlot(Long timeSlotId);
    Long findPreviousIdInRecruitment(Long recruitmentId, Long currentId);
    Long findNextIdInRecruitment(Long recruitmentId, Long currentId);
}
