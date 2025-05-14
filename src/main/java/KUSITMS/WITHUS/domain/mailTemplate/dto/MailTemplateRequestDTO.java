package KUSITMS.WITHUS.domain.mailTemplate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "메일 템플릿 요청 DTO")
public class MailTemplateRequestDTO {

    @Schema(description = "메일 템플릿 생성 요청 DTO")
    public record Create(
            @Schema(description = "템플릿 이름", example = "면접 일정 안내")
            @NotBlank String name,

            @Schema(description = "메일 제목", example = "[WITHUS] 면접 일정 안내")
            @NotBlank String subject,

            @Schema(description = "메일 본문", example = "<p>안녕하세요, {{applicantName}}님!</p>")
            @NotBlank String body
    ) {}
}
