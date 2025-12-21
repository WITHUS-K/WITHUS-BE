package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.repository;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.QEvaluationCriteria.evaluationCriteria;

@Repository
@RequiredArgsConstructor
public class EvaluationCriteriaRepositoryImpl implements EvaluationCriteriaRepository {

    private final EvaluationCriteriaJpaRepository evaluationCriteriaJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public EvaluationCriteria getById(Long id) {
        return evaluationCriteriaJpaRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.EVALUATION_CRITERIA_NOT_EXIST));
    }

    @Override
    public List<EvaluationCriteria> findByTypeAndRecruitment(EvaluationType type, Long recruitmentId) {
        return evaluationCriteriaJpaRepository.findByEvaluationTypeAndRecruitmentId(type, recruitmentId);
    }

    @Override
    public Long countByRecruitment_IdAndEvaluationType(Long recruitmentId, EvaluationType stage) {
        return evaluationCriteriaJpaRepository.countByRecruitment_IdAndEvaluationType(recruitmentId, stage);
    }

    @Override
    public List<EvaluationCriteria> findByRecruitment_IdAndEvaluationType(Long recruitmentId, EvaluationType stage) {
        return evaluationCriteriaJpaRepository.findByRecruitment_IdAndEvaluationType(recruitmentId, stage);
    }

    @Override
    public List<EvaluationCriteria> findCommonAndByOrganizationRole(Long recruitmentId, EvaluationType type, OrganizationRole organizationRole) {
        BooleanExpression roleFilter = organizationRole == null
                ? evaluationCriteria.organizationRole.isNull()
                : evaluationCriteria.organizationRole.isNull()
                .or(evaluationCriteria.organizationRole.eq(organizationRole));

        return queryFactory
                .selectFrom(evaluationCriteria)
                .where(
                        evaluationCriteria.recruitment.id.eq(recruitmentId)
                                .and(evaluationCriteria.evaluationType.eq(type))
                                .and(roleFilter)
                )
                .fetch();
    }

    @Override
    public List<EvaluationCriteria> findAllById(List<Long> criteriaIds) {
        return evaluationCriteriaJpaRepository.findAllById(criteriaIds);
    }
}
