package KUSITMS.WITHUS.domain.template.dto;

import KUSITMS.WITHUS.domain.template.enumerate.Medium;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "메일 템플릿 요청 DTO")
public class TemplateRequestDTO {

    @Schema(description = "템플릿 생성 요청 DTO")
    public record Create(
            @Schema(description = "템플릿 이름", example = "면접 일정 안내")
            @NotBlank String name,

            @Schema(description = "메일 제목", example = "[WITHUS] 면접 일정 안내")
            String subject,

            @Schema(description = "메일 본문", example = "<p>안녕하세요, {{applicantName}}님!</p>")
            @NotBlank String body,

            @Schema(description = "템플릿 타입", example = "SMS | MAIL")
            @NotNull Medium medium
            ) {}
}
