package KUSITMS.WITHUS.domain.evaluation.evaluation.repository;

import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
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
    public List<Evaluation> findAllByApplicationIdWithCriteriaAndUser(Long applicationId) {
        return queryFactory
                .selectFrom(evaluation)
                .join(evaluation.criteria).fetchJoin()
                .join(evaluation.user).fetchJoin()
                .where(evaluation.application.id.eq(applicationId))
                .fetch();
    }
}
