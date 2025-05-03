package KUSITMS.WITHUS.domain.application.applicationAnswer.dto;

import KUSITMS.WITHUS.domain.application.applicationAnswer.entity.ApplicationAnswer;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.enumerate.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ApplicationAnswerResponseDTO(
        @Schema(description = "질문 ID") Long questionId,
        @Schema(description = "질문 제목") String questionTitle,
        @Schema(description = "질문 방식 - TEXT, FILE") QuestionType questionType,
        @Schema(description = "답변 내용") String answerText,
        @Schema(description = "탑변 파일") String fileUrl
) {
    public static ApplicationAnswerResponseDTO from(ApplicationAnswer answer) {
        return new ApplicationAnswerResponseDTO(
                answer.getQuestion().getId(),
                answer.getQuestion().getTitle(),
                answer.getQuestion().getType(),
                answer.getAnswerText(),
                answer.getFileUrl()
        );
    }
}
