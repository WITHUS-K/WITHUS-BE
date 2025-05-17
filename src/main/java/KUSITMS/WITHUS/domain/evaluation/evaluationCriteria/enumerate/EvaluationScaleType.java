package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EvaluationScaleType {
    SCORE("점수제 평가"),
    LEVEL("단계 평가");

    private final String key;
}
