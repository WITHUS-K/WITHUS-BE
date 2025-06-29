package KUSITMS.WITHUS.domain.application.applicationAnswer.dto;

import KUSITMS.WITHUS.domain.application.applicationAnswer.entity.ApplicationAnswer;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.enumerate.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ApplicationAnswerResponseDTO(
        @Schema(description = "질문 ID") Long questionId,
        @Schema(description = "질문 제목") String questionTitle,
        @Schema(description = "질문 제목") String questionDescription,
        @Schema(description = "질문 방식 - TEXT, FILE") QuestionType questionType,
        @Schema(description = "답변 내용") String answerText,
        @Schema(description = "탑변 파일") String fileUrl,

        // TEXT
        @Schema(description = "최대 글자 수") Integer textLimit,
        @Schema(description = "공백 포함 여부") Boolean includeWhitespace,

        // FILE
        @Schema(description = "최대 파일 수") Integer maxFileCount,
        @Schema(description = "최대 파일 크기 (MB)") Integer maxFileSizeMb
) {
    public static ApplicationAnswerResponseDTO from(ApplicationAnswer answer) {
        DocumentQuestion question = answer.getQuestion();

        return new ApplicationAnswerResponseDTO(
                question.getId(),
                question.getTitle(),
                question.getDescription(),
                question.getType(),
                answer.getAnswerText(),
                answer.getFileUrl(),

                // TEXT
                question.getType() == QuestionType.TEXT ? question.getTextLimit() : null,
                question.getType() == QuestionType.TEXT ? question.getIncludeWhitespace() : null,

                // FILE
                question.getType() == QuestionType.FILE ? question.getMaxFileCount() : null,
                question.getType() == QuestionType.FILE ? question.getMaxFileSizeMb() : null
        );
    }
}
