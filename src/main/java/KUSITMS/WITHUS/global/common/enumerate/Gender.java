package KUSITMS.WITHUS.global.common.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {

    MALE("남"),
    FEMALE("여"),
    NONE("관리자용 기본값");

    private final String key;
}
