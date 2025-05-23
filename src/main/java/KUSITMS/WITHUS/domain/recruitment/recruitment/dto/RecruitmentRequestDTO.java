package KUSITMS.WITHUS.domain.recruitment.recruitment.dto;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationScaleType;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "리크루팅(공고) 관련 요청 DTO")
public class RecruitmentRequestDTO {

    @Schema(description = "공고 생성 요청 DTO")
    public record Upsert (
            @Schema(description = "공고 ID - null이면 생성, 아니면 수정", example = "1")
            Long recruitmentId,

            @Schema(description = "공고 제목", example = "2025-1 큐시즘 모집")
            @NotBlank String title,

            @Schema(description = "공고 내용", example = "큐시즘 학회원 모집합니다.")
            @NotBlank String content,

            @Schema(description = "등록할 포지션 이름 목록", example = "[\"백엔드\", \"디자인\"]")
            List<String> positions,

            @Schema(description = "지원서 문항 목록")
            List<DocumentQuestionRequestDTO.Create> applicationQuestions,

            @Schema(description = "서류 마감일", example = "2025-06-01")
            @NotNull @Future LocalDate documentDeadline,

            @Schema(description = "서류 발표 필수 여부", example = "true")
            Boolean isDocumentResultRequired,

            @Schema(description = "서류 발표일", example = "2025-06-10")
            @Future LocalDate documentResultDate,

            @Schema(description = "최종 발표일", example = "2025-06-20")
            @NotNull @Future LocalDate finalResultDate,

            @Schema(description = "면접 소요시간", example = "30")
            Short interviewDuration,

            @Schema(description = "조직 ID", example = "1")
            @NotNull Long organizationId,

            @Schema(description = "성별 입력 필요 여부", example = "true")
            boolean needGender,

            @Schema(description = "주소 입력 필요 여부", example = "false")
            boolean needAddress,

            @Schema(description = "학교 입력 필요 여부", example = "true")
            boolean needSchool,

            @Schema(description = "생년월일 입력 필요 여부", example = "true")
            boolean needBirthDate,

            @Schema(description = "전공 입력 필요 여부", example = "true")
            boolean needMajor,

            @Schema(description = "학적 상태 입력 필요 여부", example = "false")
            boolean needAcademicStatus,

            @Schema(description = "서류 평가 방식", example = "SCORE")EvaluationScaleType documentScaleType,
            @Schema(description = "면접 평가 방식", example = "SCORE") EvaluationScaleType interviewScaleType,

            @Schema(description = "서류 평가 기준 목록") List<EvaluationCriteriaRequestDTO.Create> documentEvaluationCriteria,
            @Schema(description = "면접 평가 기준 목록") List<EvaluationCriteriaRequestDTO.Create> interviewEvaluationCriteria,

            @Schema(description = "면접 일정 등록 필수 여부", example = "true")
            Boolean isInterviewRequired,

            @Schema(description = "면접 가능 시간 목록")
            List<AvailableTimeRangeRequestDTO> availableTimeRanges
    ) {}

    @Schema(description = "공고 수정 요청 DTO")
    public record Update (
            @Schema(description = "공고 제목", example = "2025-2 큐시즘 모집")
            @NotBlank String title,

            @Schema(description = "공고 내용", example = "큐시즘 학회원 추가 모집합니다.")
            @NotBlank String content,

            @Schema(description = "첨부 파일 URL", example = "https://withus.com/files/recruit_v2.pdf")
            String fileUrl,

            @Schema(description = "등록할 포지션 이름 목록", example = "[백엔드, 디자인]")
            List<String> positions,

            @Schema(description = "서류 마감일", example = "2025-06-01")
            @NotNull @Future LocalDate documentDeadline,

            @Schema(description = "서류 발표 필수 여부", example = "true")
            Boolean isDocumentResultRequired,

            @Schema(description = "서류 발표일", example = "2025-06-10")
            @Future LocalDate documentResultDate,

            @Schema(description = "최종 발표일", example = "2025-06-20")
            @NotNull @Future LocalDate finalResultDate,

            @Schema(description = "면접 소요시간")
            Short interviewDuration,

            @Schema(description = "성별 수집 여부") boolean needGender,
            @Schema(description = "주소 수집 여부") boolean needAddress,
            @Schema(description = "학교 수집 여부") boolean needSchool,
            @Schema(description = "생년월일 수집 여부") boolean needBirthDate,
            @Schema(description = "학적 상태 수집 여부") boolean needAcademicStatus,

            @Schema(description = "임시 저장 여부") boolean isTemporary,

            @Schema(description = "서류 평가 방식", example = "SCORE") EvaluationScaleType documentScaleType,
            @Schema(description = "면접 평가 방식", example = "SCORE") EvaluationScaleType interviewScaleType,

            @Schema(description = "서류 평가 기준 목록") List<EvaluationCriteriaRequestDTO.Create> documentEvaluationCriteria,
            @Schema(description = "면접 평가 기준 목록") List<EvaluationCriteriaRequestDTO.Create> interviewEvaluationCriteria,

            @Schema(description = "면접 일정 등록 필수 여부", example = "true") Boolean isInterviewRequired,

            @Schema(description = "면접 가능 시간 목록")
            List<AvailableTimeRangeRequestDTO> availableTimeRanges
    ) {}

}
