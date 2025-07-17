package KUSITMS.WITHUS.domain.application.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationStatus {

    PENDING("보류"), // 지원서 생성 시 default status
    DOX_PASS("서류 합격"),
    DOX_FAIL("서류 불합격"),
    DOX_PENDING("서류 보류"),
    INTERVIEW_PASS("면접 합격"),
    INTERVIEW_FAIL("면접 불합격"),
    INTERVIEW_PENDING("면접 보류");

    private final String key;
}
