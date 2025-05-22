package KUSITMS.WITHUS.domain.user.user.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProfileColor {
    RED("red"),
    ORANGE("orange"),
    YELLOW("yellow"),
    GREEN("green"),
    BLUESKY("bluesky"),
    BLUE("blue"),
    PURPLE("purple"),
    PINK("pink"),
    GRAY("gray"),
    DARKGRAY("darkgray");;

    private final String key;
}
