package KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto;

import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.enumerate.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "서류 질문 관련 응답 DTO")
public class DocumentQuestionResponseDTO {

    @Schema(description = "서류 질문 생성 응답 DTO")
    public record Create(
            @Schema(description = "질문 ID") Long questionId,
            @Schema(description = "질문 제목") String title
    ) {
        public static Create from(DocumentQuestion question) {
            return new Create(question.getId(), question.getTitle());
        }
    }

    @Schema(description = "서류 질문 요약 응답 DTO")
    public record Summary(
            @Schema(description = "질문 ID") Long questionId,
            @Schema(description = "질문 제목") String title,
            @Schema(description = "질문 설명") String description,
            @Schema(description = "질문 형식") QuestionType type,
            @Schema(description = "필수 여부") boolean required
    ) {
        public static Summary from(DocumentQuestion question) {
            return new Summary(
                    question.getId(),
                    question.getTitle(),
                    question.getDescription(),
                    question.getType(),
                    question.isRequired()
            );
        }
    }
}
