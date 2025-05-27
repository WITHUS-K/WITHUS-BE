package KUSITMS.WITHUS.domain.recruitment.position.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@RequiredArgsConstructor
public enum PositionColor {
    RED("red"),
    ORANGE("orange"),
    YELLOW("yellow"),
    GREEN("green"),
    BLUESKY("bluesky"),
    BLUE("blue"),
    PURPLE("purple"),
    PINK("pink"),
    GRAY("gray"),
    DARKGRAY("darkgray");

    private final String key;

    public static String getRandomColor() {
        PositionColor[] values = PositionColor.values();
        return values[ThreadLocalRandom.current().nextInt(values.length)].getKey();
    }
}

