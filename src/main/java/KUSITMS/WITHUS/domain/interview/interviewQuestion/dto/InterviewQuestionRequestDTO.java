package KUSITMS.WITHUS.domain.interview.interviewQuestion.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "면접질문 관련 요청 DTO")
public class InterviewQuestionRequestDTO {

    @Schema(description = "면접질문 생성 요청")
    public record Create(
            @Schema(description = "면접질문 내용", example = "지원동기가 무엇인가요?") String content
    ) {}
}
