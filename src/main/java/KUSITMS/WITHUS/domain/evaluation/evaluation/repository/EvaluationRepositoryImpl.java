package KUSITMS.WITHUS.domain.evaluation.evaluation.repository;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.evaluation.evaluation.entity.QEvaluation.evaluation;

@Repository
@RequiredArgsConstructor
public class EvaluationRepositoryImpl implements EvaluationRepository {

    private final EvaluationJpaRepository evaluationJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Evaluation save(Evaluation evaluation) {
        return evaluationJpaRepository.save(evaluation);
    }

    @Override
    public void saveAll(List<Evaluation> evaluations) {
        evaluationJpaRepository.saveAll(evaluations);
    }

    @Override
    public boolean existsByApplicationAndCriteriaAndUser(Long applicationId, Long criteriaId, Long userId) {
        return evaluationJpaRepository.existsByApplicationIdAndCriteriaIdAndUserId(applicationId, criteriaId, userId);
    }

    @Override
    public List<Evaluation> findEvaluationsForApplication(Long applicationId) {
        return queryFactory
                .selectFrom(evaluation)
                .join(evaluation.criteria).fetchJoin()
                .join(evaluation.user).fetchJoin()
                .where(evaluation.application.id.eq(applicationId))
                .fetch();
    }

    @Override
    public List<Evaluation> findByApplicationAndUserAndCriteriaIn(Application application, User user, List<EvaluationCriteria> criterias) {
        return evaluationJpaRepository.findByApplicationAndUserAndCriteriaIn(application, user, criterias);
    }

    @Override
    public long countFullyEvaluatedApplications(
            Long recruitmentId,
            Long positionId,
            EvaluationType stage,
            long requiredCriteriaCount
    ) {
        return queryFactory
                .select(evaluation.application.id)
                .from(evaluation)
                .where(
                        evaluation.application.recruitment.id.eq(recruitmentId),
                        evaluation.application.position.id.eq(positionId),
                        evaluation.criteria.evaluationType.eq(stage)
                )
                .groupBy(evaluation.application.id)
                .having(evaluation.criteria.id.countDistinct().eq(requiredCriteriaCount))
                .fetch()
                .size();
    }

    @Override
    public long countByApplication_IdAndUser_IdAndCriteria_IdIn(Long applicationId, Long userId, List<Long> criteriaIds) {
        return evaluationJpaRepository.countByApplication_IdAndUser_IdAndCriteria_IdIn(applicationId, userId, criteriaIds);
    }

    @Override
    public long countByApplication_IdAndUser_IdAndCriteria_EvaluationType(Long id, Long userId, EvaluationType evaluationType) {
        return evaluationJpaRepository.countByApplication_IdAndUser_IdAndCriteria_EvaluationType(id, userId, evaluationType);
    }

    @Override
    public void deleteAll(List<Evaluation> existingEvaluations) {
        evaluationJpaRepository.deleteAll(existingEvaluations);
    }
}
