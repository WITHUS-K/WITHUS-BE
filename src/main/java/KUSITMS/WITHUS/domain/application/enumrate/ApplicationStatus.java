package KUSITMS.WITHUS.domain.application.enumrate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationStatus {

    PENDING("보류"),
    DOX_PASS("서류 합격"),
    DOX_FAIL("서류 불합격"),
    INTERVIEW_PASS("면접 합격"),
    INTERVIEW_FAIL("면접 불합격");

    private final String key;
}
