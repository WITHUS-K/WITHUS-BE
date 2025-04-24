package KUSITMS.WITHUS.domain.application.application.dto;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.availability.entity.ApplicantAvailability;
import KUSITMS.WITHUS.domain.application.comment.dto.CommentResponseDTO;
import KUSITMS.WITHUS.domain.application.comment.enumerate.CommentType;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import KUSITMS.WITHUS.domain.application.interviewQuestion.dto.InterviewQuestionResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "지원서 응답 DTO")
public class ApplicationResponseDTO {

    @Schema(description = "지원서 상세 조회 응답 DTO")
    public record Detail(
            @Schema(description = "지원서 ID") Long id,
            @Schema(description = "지원자 이름") String name,
            @Schema(description = "성별") Gender gender,
            @Schema(description = "이메일") String email,
            @Schema(description = "전화번호") String phoneNumber,
            @Schema(description = "대학명") String university,
            @Schema(description = "전공") String major,
            @Schema(description = "생년월일") LocalDate birthDate,
            @Schema(description = "이미지 URL") String imageUrl,
            @Schema(description = "상태") ApplicationStatus status,
            @Schema(description = "면접 가능 시간") List<LocalDateTime> availableTimes,
            @Schema(description = "면접 질문 목록") List<InterviewQuestionResponseDTO.Detail> interviewQuestions,
            @Schema(description = "면접 평가 목록") List<EvaluationResponseDTO.Detail> evaluations,
            @Schema(description = "서류 코맨트 목록") List<CommentResponseDTO.Detail> documentComments,
            @Schema(description = "면접 코맨트 목록") List<CommentResponseDTO.Detail> interviewComments
    ) {
        public static Detail from(Application application, List<ApplicantAvailability> availabilityList, List<Evaluation> evaluationList) {
            List<LocalDateTime> times = availabilityList.stream()
                    .map(ApplicantAvailability::getAvailableTime)
                    .toList();

            List<InterviewQuestionResponseDTO.Detail> questions = application.getInterviewQuestions().stream()
                    .map(InterviewQuestionResponseDTO.Detail::from)
                    .toList();

            List<EvaluationResponseDTO.Detail> evaluations = evaluationList.stream()
                    .map(EvaluationResponseDTO.Detail::from)
                    .toList();

            List<KUSITMS.WITHUS.domain.application.comment.dto.CommentResponseDTO.Detail> documentComments = application.getComments().stream()
                    .filter(c -> c.getType() == CommentType.DOCUMENT)
                    .map(CommentResponseDTO.Detail::from)
                    .toList();

            List<CommentResponseDTO.Detail> interviewComments = application.getComments().stream()
                    .filter(c -> c.getType() == CommentType.INTERVIEW)
                    .map(CommentResponseDTO.Detail::from)
                    .toList();

            return new Detail(
                    application.getId(),
                    application.getName(),
                    application.getGender(),
                    application.getEmail(),
                    application.getPhoneNumber(),
                    application.getUniversity(),
                    application.getMajor(),
                    application.getBirthDate(),
                    application.getImageUrl(),
                    application.getStatus(),
                    times,
                    questions,
                    evaluations,
                    documentComments,
                    interviewComments
            );
        }
    }

    @Schema(description = "지원서 요약 응답 DTO")
    public record Summary(
            @Schema(description = "지원서 ID") Long id,
            @Schema(description = "지원자 이름") String name,
            @Schema(description = "이메일") String email,
            @Schema(description = "파트명") String positionName,
            @Schema(description = "상태") ApplicationStatus status
    ) {
        public static Summary from(Application application) {
            return new Summary(
                    application.getId(),
                    application.getName(),
                    application.getEmail(),
                    application.getPosition() != null ? application.getPosition().getName() : null,
                    application.getStatus()
            );
        }
    }
}
