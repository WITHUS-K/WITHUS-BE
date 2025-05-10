package KUSITMS.WITHUS.domain.interview.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewRole {
    INTERVIEWER("면접관"),
    ASSISTANT("안내자");

    private final String key;
}
