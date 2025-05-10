package KUSITMS.WITHUS.domain.application.application.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AcademicStatus {
    ENROLLED("재학"),
    GRADUATED("졸업"),
    LEAVE_OF_ABSENCE("휴학"),
    DEFERRED("유예");

    private final String key;
}
