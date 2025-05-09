package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EvaluationScaleType {
    SCORE("점수제 평가"),
    THREE_LEVEL("3단계 평가"),
    FIVE_LEVEL("5단계 평가");

    private final String key;

    public String getKey() {
        return this.key;
    }
}
