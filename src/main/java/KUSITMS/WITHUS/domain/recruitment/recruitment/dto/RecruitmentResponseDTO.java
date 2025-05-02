package KUSITMS.WITHUS.domain.recruitment.recruitment.dto;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationScaleType;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.common.annotation.DateFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "리크루팅(공고) 관련 응답 DTO")
public class RecruitmentResponseDTO {

    @Schema(description = "공고 생성 응답 DTO")
    public record Create(
            @Schema(description = "공고 Id") Long recruitmentId
    ) {
        public static Create from(Recruitment recruitment) {
            return new Create(
                    recruitment.getId()
            );
        }
    }

    @Schema(description = "공고 상세 정보 응답 DTO")
    public record Detail(
            @Schema(description = "공고 Id") Long recruitmentId,
            @Schema(description = "공고 제목") String title,
            @Schema(description = "공고 내용") String content,
            @Schema(description = "첨부 파일 URL") String fileUrl,
            @Schema(description = "서류 마감일") @DateFormat LocalDate documentDeadline,
            @Schema(description = "서류 발표일") @DateFormat LocalDate documentResultDate,
            @Schema(description = "최종 발표일") @DateFormat LocalDate finalResultDate,
            @Schema(description = "조직명") String organizationName,
            @Schema(description = "평가 방식") EvaluationScaleType scaleType,
            @Schema(description = "서류 평가 기준 목록") List<String> documentEvaluationCriteria,
            @Schema(description = "면접 평가 기준 목록") List<String> interviewEvaluationCriteria,
            @Schema(description = "지원서 질문 목록") List<DocumentQuestionResponseDTO.Summary> applicationQuestions,
            @Schema(description = "면접 가능 시간 목록") List<AvailableTimeRangeResponseDTO> availableTimeRanges
    ) {
        public static Detail from(Recruitment recruitment) {
            List<String> documentCriteria = recruitment.getEvaluationCriteriaList().stream()
                    .filter(c -> c.getEvaluationType() == EvaluationType.DOCUMENT)
                    .map(EvaluationCriteria::getContent)
                    .toList();

            List<String> interviewCriteria = recruitment.getEvaluationCriteriaList().stream()
                    .filter(c -> c.getEvaluationType() == EvaluationType.INTERVIEW)
                    .map(EvaluationCriteria::getContent)
                    .toList();

            List<DocumentQuestionResponseDTO.Summary> questions = recruitment.getQuestions().stream()
                    .map(DocumentQuestionResponseDTO.Summary::from)
                    .toList();

            List<AvailableTimeRangeResponseDTO> timeRanges = recruitment.getAvailableTimeRanges().stream()
                    .map(AvailableTimeRangeResponseDTO::from)
                    .toList();

            return new Detail(
                    recruitment.getId(),
                    recruitment.getTitle(),
                    recruitment.getContent(),
                    recruitment.getFileUrl(),
                    recruitment.getDocumentDeadline(),
                    recruitment.getDocumentResultDate(),
                    recruitment.getFinalResultDate(),
                    recruitment.getOrganization().getName(),
                    recruitment.getScaleType(),
                    documentCriteria,
                    interviewCriteria,
                    questions,
                    timeRanges
            );
        }
    }


    @Schema(description = "공고 수정 응답 DTO")
    public record Update(
            @Schema(description = "공고 Id") Long recruitmentId
    ) {
        public static Update from(Recruitment recruitment) {
            return new Update(
                    recruitment.getId()
            );
        }
    }

    @Schema(description = "공고 요약 정보 응답 DTO")
    public record Summary(
            @Schema(description = "공고 Id") Long recruitmentId,
            @Schema(description = "공고 제목") String title,
            @Schema(description = "서류 마감일") @DateFormat LocalDate documentDeadline,
            @Schema(description = "서류 발표일") @DateFormat LocalDate documentResultDate,
            @Schema(description = "최종 발표일") @DateFormat LocalDate finalResultDate,
            @Schema(description = "조직명") String organizationName
    ) {
        public static Summary from(Recruitment recruitment) {
            return new Summary(
                    recruitment.getId(),
                    recruitment.getTitle(),
                    recruitment.getDocumentDeadline(),
                    recruitment.getDocumentResultDate(),
                    recruitment.getFinalResultDate(),
                    recruitment.getOrganization().getName()
            );
        }
    }
}
