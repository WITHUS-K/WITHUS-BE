package KUSITMS.WITHUS.domain.application.application.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EvaluationStatus {
    ALL("전체"),
    EVALUATED("평가 완료"),
    NOT_EVALUATED("평가 전");

    private final String key;
}
