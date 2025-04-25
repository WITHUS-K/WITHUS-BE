package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.repository;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EvaluationCriteriaRepositoryImpl implements EvaluationCriteriaRepository {

    private final EvaluationCriteriaJpaRepository evaluationCriteriaJpaRepository;

    @Override
    public EvaluationCriteria getById(Long id) {
        return evaluationCriteriaJpaRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.EVALUATION_CRITERIA_NOT_EXIST));
    }
}
