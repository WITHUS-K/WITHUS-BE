package KUSITMS.WITHUS.global.response;

public record PaginationMeta(
        int page,
        int size,
        int totalPages,
        long totalElements,
        boolean isLast
) {}
