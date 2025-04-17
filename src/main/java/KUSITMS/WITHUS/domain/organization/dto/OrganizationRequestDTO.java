package KUSITMS.WITHUS.domain.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "조직(동아리) 관련 요청 DTO")
public class OrganizationRequestDTO {

    @Schema(description = "조직 생성 요청 DTO")
    public record Create (
            @Schema(description = "조직 이름", example = "KUSITMS")
            @NotBlank String name
    ) {}

    @Schema(description = "조직 수정 요청 DTO")
    public record Update (
            @Schema(description = "조직 이름", example = "WITHUS")
            @NotBlank String name
    ) {}
}
