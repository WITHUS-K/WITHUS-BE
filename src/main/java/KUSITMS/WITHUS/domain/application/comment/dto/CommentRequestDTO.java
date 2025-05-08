package KUSITMS.WITHUS.domain.application.comment.dto;

import KUSITMS.WITHUS.domain.application.comment.enumerate.CommentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "코맨트 관련 요청 DTO")
public class CommentRequestDTO {

    @Schema(name = "CommentRequestDTO.Create", description = "코맨트 생성 요청 DTO")
    public record Create(
            @Schema(description = "코맨트 내용", example = "큐시즘 경험 있음")
            @NotBlank String content,

            @Schema(description = "코맨트 유형(서류, 면접)", example = "INTERVIEW")
            @NotNull CommentType type
    ) {}

    @Schema(name = "CommentRequestDTO.Update", description = "코맨트 수정 요청 DTO")
    public record Update (
            @Schema(description = "코맨트 내용", example = "큐시즘 경험 있음")
            @NotBlank String content
    ) {}

}
