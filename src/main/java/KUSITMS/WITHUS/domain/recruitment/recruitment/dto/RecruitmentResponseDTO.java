package KUSITMS.WITHUS.domain.recruitment.recruitment.dto;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationScaleType;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.position.dto.PositionResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.common.annotation.DateFormatDot;
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
            @Schema(description = "고유 슬러그 값") String UrlSlug,
            @Schema(description = "공고 제목") String title,
            @Schema(description = "공고 내용") String content,
            @Schema(description = "첨부 파일 URL") String fileUrl,
            @Schema(description = "성별 입력 필요 여부", example = "true") boolean needGender,
            @Schema(description = "주소 입력 필요 여부", example = "false") boolean needAddress,
            @Schema(description = "학교 입력 필요 여부", example = "true") boolean needSchool,
            @Schema(description = "생년월일 입력 필요 여부", example = "true") boolean needBirthDate,
            @Schema(description = "전공 입력 필요 여부", example = "true") boolean needMajor,
            @Schema(description = "학적 상태 입력 필요 여부", example = "false") boolean needAcademicStatus,
            @Schema(description = "포지션 목록") List<PositionResponseDTO.Detail> positions,
            @Schema(description = "서류 마감일") @DateFormatDot LocalDate documentDeadline,
            @Schema(description = "서류 발표일") @DateFormatDot LocalDate documentResultDate,
            @Schema(description = "최종 발표일") @DateFormatDot LocalDate finalResultDate,
            @Schema(description = "면접 소요시간") Short interviewDuration,
            @Schema(description = "조직명") String organizationName,
            @Schema(description = "서류 평가 방식") EvaluationScaleType documentScaleType,
            @Schema(description = "면접 평가 방식") EvaluationScaleType interviewScaleType,
            @Schema(description = "서류 평가 기준 상세 목록") List<EvaluationCriteriaResponseDTO.Detail> documentEvaluationCriteria,
            @Schema(description = "면접 평가 기준 상세 목록") List<EvaluationCriteriaResponseDTO.Detail> interviewEvaluationCriteria,
            @Schema(description = "지원서 질문 목록") List<DocumentQuestionResponseDTO.Summary> applicationQuestions,
            @Schema(description = "면접 가능 시간 목록") List<AvailableTimeRangeResponseDTO> availableTimeRanges
    ) {
        public static Detail from(Recruitment recruitment) {
            List<EvaluationCriteriaResponseDTO.Detail> documentCriteria = recruitment.getEvaluationCriteriaList().stream()
                    .filter(c -> c.getEvaluationType() == EvaluationType.DOCUMENT)
                    .map(EvaluationCriteriaResponseDTO.Detail::from)
                    .toList();

            List<EvaluationCriteriaResponseDTO.Detail> interviewCriteria = recruitment.getEvaluationCriteriaList().stream()
                    .filter(c -> c.getEvaluationType() == EvaluationType.INTERVIEW)
                    .map(EvaluationCriteriaResponseDTO.Detail::from)
                    .toList();

            List<DocumentQuestionResponseDTO.Summary> questions = recruitment.getQuestions().stream()
                    .map(DocumentQuestionResponseDTO.Summary::from)
                    .toList();

            List<AvailableTimeRangeResponseDTO> timeRanges = recruitment.getAvailableTimeRanges().stream()
                    .map(AvailableTimeRangeResponseDTO::from)
                    .toList();

            List<PositionResponseDTO.Detail> positions = recruitment.getPositions().stream()
                    .map(PositionResponseDTO.Detail::from)
                    .toList();

            return new Detail(
                    recruitment.getId(),
                    recruitment.getUrlSlug(),
                    recruitment.getTitle(),
                    recruitment.getContent(),
                    recruitment.getFileUrl(),
                    recruitment.isNeedGender(),
                    recruitment.isNeedAddress(),
                    recruitment.isNeedSchool(),
                    recruitment.isNeedBirthDate(),
                    recruitment.isNeedMajor(),
                    recruitment.isNeedAcademicStatus(),
                    positions,
                    recruitment.getDocumentDeadline(),
                    recruitment.getDocumentResultDate(),
                    recruitment.getFinalResultDate(),
                    recruitment.getInterviewDuration(),
                    recruitment.getOrganization().getName(),
                    recruitment.getDocumentScaleType(),
                    recruitment.getInterviewScaleType(),
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
            @Schema(description = "서류 마감일") @DateFormatDot LocalDate documentDeadline,
            @Schema(description = "서류 발표일") @DateFormatDot LocalDate documentResultDate,
            @Schema(description = "최종 발표일") @DateFormatDot LocalDate finalResultDate,
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

    @Schema(description = "공고 최소한의 정보 응답 DTO")
    public record Simple(
            @Schema(description = "공고 Id") Long recruitmentId,
            @Schema(description = "공고 제목") String title
    ) {
        public static Simple from(Recruitment recruitment) {
            return new Simple(
                    recruitment.getId(),
                    recruitment.getTitle()
            );
        }
    }
}
