package KUSITMS.WITHUS.domain.application.applicationAcquaintance.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "지인 표시 응답 DTO")
public class ApplicationAcquaintanceResponseDTO {

    @Schema(description = "지인 표시 토글 응답 DTO")
    public record Toggle(
            @Schema(description = "지인 표시 여부", example = "true") boolean acquainted
    ) {}
}
