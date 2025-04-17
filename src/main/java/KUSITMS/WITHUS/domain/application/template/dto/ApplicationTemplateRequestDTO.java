package KUSITMS.WITHUS.domain.application.template.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "지원서 양식 관련 요청 DTO")
public class ApplicationTemplateRequestDTO {

    @Schema(description = "지원서 양식 생성 요청 DTO")
    public record Create(
            @Schema(description = "양식 제목", example = "큐시즘 2025 상반기 지원서 양식")
            @NotBlank String title,

            @Schema(description = "연결된 리크루팅 ID", example = "1")
            @NotNull Long recruitmentId
    ) {}
}
