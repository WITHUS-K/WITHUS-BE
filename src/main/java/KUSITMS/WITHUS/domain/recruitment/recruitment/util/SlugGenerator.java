package KUSITMS.WITHUS.domain.recruitment.recruitment.util;

import java.util.concurrent.ThreadLocalRandom;

public class SlugGenerator {
    private static final String BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateRandomSlug() {
        long timestamp = System.currentTimeMillis();
        long random = ThreadLocalRandom.current().nextLong(1000, 9999);
        return encodeBase62(timestamp) + encodeBase62(random);
    }

    private static String encodeBase62(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(BASE62.charAt((int)(value % 62)));
            value /= 62;
        }
        return sb.reverse().toString();
    }
}

