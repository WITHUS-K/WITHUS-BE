package KUSITMS.WITHUS.domain.interview.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "면접 관련 요청 DTO")
public class InterviewRequestDTO {

    @Schema(description = "면접 생성 요청 DTO")
    public record Create(
            @Schema(description = "면접 제목", example = "큐시즘 면접")
            @NotBlank String title,

            @Schema(description = "공고 ID", example = "1")
            @NotNull Long recruitmentId
    ) {}
}

