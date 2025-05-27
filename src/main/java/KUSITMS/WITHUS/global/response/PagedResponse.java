package KUSITMS.WITHUS.global.response;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(
        List<T> data,
        PaginationMeta pagination,
        ApplicationResponseDTO.StageCount counts
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
                ),
                null
        );
    }

    public static <T> PagedResponse<T> from(
            Page<T> page,
            ApplicationResponseDTO.StageCount counts
    ) {
        return new PagedResponse<>(
                page.getContent(),
                new PaginationMeta(
                        page.getNumber() + 1,
                        page.getSize(),
                        page.getTotalPages(),
                        page.getTotalElements(),
                        page.isLast()
                ),
                counts
        );
    }
}
