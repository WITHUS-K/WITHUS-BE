package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.repository;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;

import java.util.List;

public interface EvaluationCriteriaRepository {
    EvaluationCriteria getById(Long id);
    List<EvaluationCriteria> findByTypeAndRecruitment(EvaluationType type, Long recruitmentId);
    Long countByRecruitment_IdAndEvaluationType(Long recruitmentId, EvaluationType stage);
    List<EvaluationCriteria> findByRecruitment_IdAndEvaluationType(Long recruitmentId, EvaluationType stage);
    List<EvaluationCriteria> findCommonAndByOrganizationRole(Long recruitmentId, EvaluationType type, OrganizationRole organizationRole);
    List<EvaluationCriteria> findAllById(List<Long> criteriaIds);
}
