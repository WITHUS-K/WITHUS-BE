package KUSITMS.WITHUS.domain.application.position.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "파트 관련 요청 DTO")
public class PositionRequestDTO {

    @Schema(description = "파트 생성 요청 DTO")
    public record Create(
            @Schema(description = "파트 이름", example = "백엔드")
            @NotBlank String name,

            @Schema(description = "조직 ID", example = "1")
            @NotNull Long organizationId
    ) {}
}
