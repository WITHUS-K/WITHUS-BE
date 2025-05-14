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

    public interface QuestionSummary {}

    @Schema(description = "텍스트형 서류 질문 요약 DTO")
    public record TextQuestionSummary(
            @Schema(description = "질문 ID") Long questionId,
            @Schema(description = "질문 제목") String title,
            @Schema(description = "질문 설명") String description,
            @Schema(description = "질문 형식") QuestionType type,
            @Schema(description = "필수 여부") boolean required,
            @Schema(description = "적용 파트 이름") String positionName,
            @Schema(description = "텍스트 글자 수 제한") Integer textLimit,
            @Schema(description = "공백 포함 여부") Boolean includeWhitespace
    ) implements QuestionSummary {
        public static TextQuestionSummary from(DocumentQuestion question) {
            return new TextQuestionSummary(
                    question.getId(),
                    question.getTitle(),
                    question.getDescription(),
                    question.getType(),
                    question.isRequired(),
                    question.getPosition() != null ? question.getPosition().getName() : "공통",
                    question.getTextLimit(),
                    question.getIncludeWhitespace()
            );
        }
    }

    @Schema(description = "파일형 서류 질문 요약 DTO")
    public record FileQuestionSummary(
            @Schema(description = "질문 ID") Long questionId,
            @Schema(description = "질문 제목") String title,
            @Schema(description = "질문 설명") String description,
            @Schema(description = "질문 형식") QuestionType type,
            @Schema(description = "필수 여부") boolean required,
            @Schema(description = "적용 파트 이름") String positionName,
            @Schema(description = "최대 파일 개수") Integer maxFileCount,
            @Schema(description = "최대 파일 크기(MB)") Integer maxFileSizeMb
    ) implements QuestionSummary {
        public static FileQuestionSummary from(DocumentQuestion question) {
            return new FileQuestionSummary(
                    question.getId(),
                    question.getTitle(),
                    question.getDescription(),
                    question.getType(),
                    question.isRequired(),
                    question.getPosition() != null ? question.getPosition().getName() : "공통",
                    question.getMaxFileCount(),
                    question.getMaxFileSizeMb()
            );
        }
    }
}
