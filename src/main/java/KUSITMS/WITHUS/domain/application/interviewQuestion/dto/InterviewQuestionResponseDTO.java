package KUSITMS.WITHUS.domain.application.interviewQuestion.dto;

import KUSITMS.WITHUS.domain.application.interviewQuestion.entity.InterviewQuestion;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "면접질문 관련 응답 DTO")
public class InterviewQuestionResponseDTO {

    @Schema(description = "면접질문 생성 응답 DTO")
    public record Create(
            @Schema(description = "면접질문 ID") Long id,
            @Schema(description = "내용") String content
    ) {
        public static Create from(InterviewQuestion interviewQuestion) {
            return new Create(
                    interviewQuestion.getId(),
                    interviewQuestion.getContent()
            );
        }
    }
    @Schema(description = "면접질문 조회 응답 DTO")
    public record Detail(
            @Schema(description = "면접질문 ID") Long id,
            @Schema(description = "내용") String content,
            @Schema(description = "질문자 정보") UserResponseDTO.Summary user
            ) {
        public static Detail from(InterviewQuestion interviewQuestion) {
            return new Detail(
                    interviewQuestion.getId(),
                    interviewQuestion.getContent(),
                    UserResponseDTO.Summary.from(interviewQuestion.getUser())
            );
        }
    }

}
