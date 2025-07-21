package KUSITMS.WITHUS.global.infra.upload.util;

public class FileSizeConverter {
    private static final double BYTE_TO_MB = 1024.0 * 1024.0;

    /**
     * Byte 단위를 MB로 변환 (소수점 1자리까지 반올림)
     */
    public static double toMegabytes(long bytes) {
        return Math.round((bytes / BYTE_TO_MB) * 10) / 10.0;
    }
}
