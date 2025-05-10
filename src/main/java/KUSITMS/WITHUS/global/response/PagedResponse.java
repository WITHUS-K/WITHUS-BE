package KUSITMS.WITHUS.global.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(
        List<T> data,
        PaginationMeta pagination
) {
    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                new PaginationMeta(
                        page.getNumber() + 1,
                        page.getSize(),
                        page.getTotalPages(),
                        page.getTotalElements(),
                        page.isLast()
                )
        );
    }
}
