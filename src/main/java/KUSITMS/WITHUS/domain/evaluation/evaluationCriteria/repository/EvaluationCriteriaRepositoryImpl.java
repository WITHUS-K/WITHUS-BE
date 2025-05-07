package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.repository;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.QEvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
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
    public List<EvaluationCriteria> findByTypeAndPositionIdOrCommon(EvaluationType evaluationType, Long positionId) {
        return queryFactory
                .selectFrom(evaluationCriteria)
                .where(
                        evaluationCriteria.evaluationType.eq(evaluationType)
                                .and(evaluationCriteria.position.isNull()
                                        .or(evaluationCriteria.position.id.eq(positionId)))
                )
                .fetch();
    }

}
