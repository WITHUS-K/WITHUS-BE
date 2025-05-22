package KUSITMS.WITHUS.domain.application.application.enumerate;

import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;

import java.util.List;

public enum AdminStageFilter {
    DOCUMENT,      // 서류
    INTERVIEW,     // 면접
    FINAL_PASS,    // 최종 합격
    FAIL;          // 불합격

    public List<ApplicationStatus> toStatusList() {
        return switch(this) {
            case DOCUMENT -> List.of(ApplicationStatus.values());
            case INTERVIEW -> List.of(
                    ApplicationStatus.PENDING,
                    ApplicationStatus.DOX_PASS,
                    ApplicationStatus.INTERVIEW_PASS,
                    ApplicationStatus.INTERVIEW_FAIL
            );
            case FINAL_PASS -> List.of(
                    ApplicationStatus.INTERVIEW_PASS
            );
            case FAIL -> List.of(
                    ApplicationStatus.DOX_FAIL,
                    ApplicationStatus.INTERVIEW_FAIL
            );
        };
    }
}
