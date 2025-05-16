package KUSITMS.WITHUS.domain.evaluation.evaluation.repository;

import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
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
}
