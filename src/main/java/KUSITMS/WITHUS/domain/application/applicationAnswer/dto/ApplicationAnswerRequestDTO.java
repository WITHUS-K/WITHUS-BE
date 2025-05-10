package KUSITMS.WITHUS.domain.application.applicationAnswer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "지원서 질문에 대한 답변 관련 요청 DTO")
public record ApplicationAnswerRequestDTO(
        @Schema(description = "질문 항목 ID", example = "1")
        @NotNull Long questionId,

        @Schema(description = "답변 내용", example = "저는 ~한 이유로 큐시즘에 지원했습니다.")
        String answerText,

        @Schema(description = "파일명", example = "profile.pdf")
        String fileName
) {}
