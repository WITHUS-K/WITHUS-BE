package KUSITMS.WITHUS.domain.recruitment.recruitment.dto;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationScaleType;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.entity.AvailableTimeRange;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.position.dto.PositionResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.global.common.annotation.DateFormatDot;
import KUSITMS.WITHUS.global.common.annotation.DateFormatSlash;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
            @Schema(description = "서류 합격 발표 필수 여부") boolean isDocumentResultRequired,
            @Schema(description = "서류 발표일") @DateFormatDot LocalDate documentResultDate,
            @Schema(description = "최종 발표일") @DateFormatDot LocalDate finalResultDate,
            @Schema(description = "면접 소요시간") Short interviewDuration,
            @Schema(description = "조직명") String organizationName,
            @Schema(description = "서류 평가 방식") EvaluationScaleType documentScaleType,
            @Schema(description = "면접 평가 방식") EvaluationScaleType interviewScaleType,
            @Schema(description = "서류 평가 기준 상세 목록") List<EvaluationCriteriaResponseDTO.Detail> documentEvaluationCriteria,
            @Schema(description = "면접 평가 기준 상세 목록") List<EvaluationCriteriaResponseDTO.Detail> interviewEvaluationCriteria,
            @Schema(description = "지원서 질문 목록") List<DocumentQuestionResponseDTO.QuestionSummary> applicationQuestions,
            @Schema(description = "면접 일정 등록 필수 여부") boolean isInterviewRequired,
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

            List<DocumentQuestionResponseDTO.QuestionSummary> questions = recruitment.getQuestions().stream()
                    .map(q -> (DocumentQuestionResponseDTO.QuestionSummary) switch (q.getType()) {
                        case TEXT -> DocumentQuestionResponseDTO.TextQuestionSummary.from(q);
                        case FILE -> DocumentQuestionResponseDTO.FileQuestionSummary.from(q);
                    })
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
                    recruitment.isDocumentResultRequired(),
                    recruitment.getDocumentResultDate(),
                    recruitment.getFinalResultDate(),
                    recruitment.getInterviewDuration(),
                    recruitment.getOrganization().getName(),
                    recruitment.getDocumentScaleType(),
                    recruitment.getInterviewScaleType(),
                    documentCriteria,
                    interviewCriteria,
                    questions,
                    recruitment.isInterviewRequired(),
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
            @Schema(description = "조직명") String organizationName,
            @Schema(description = "Slug 값") String urlSlug,
            @Schema(description = "포지션별 지원자 수") List<PositionResponseDTO.SummaryForRecruitment> positionSummaries
    ) {
        public static Summary from(Recruitment recruitment) {
            List<PositionResponseDTO.SummaryForRecruitment> positionSummaries = recruitment.getPositions().stream()
                    .map(PositionResponseDTO.SummaryForRecruitment::from)
                    .toList();

            return new Summary(
                    recruitment.getId(),
                    recruitment.getTitle(),
                    recruitment.getDocumentDeadline(),
                    recruitment.getDocumentResultDate(),
                    recruitment.getFinalResultDate(),
                    recruitment.getOrganization().getName(),
                    recruitment.getUrlSlug(),
                    positionSummaries
            );
        }
    }

    @Schema(description = "홈 페이지 용 공고 요약 정보 응답 DTO")
    public record SummaryForHome(
            @Schema(description = "공고 Id") Long recruitmentId,
            @Schema(description = "공고 제목") String title,

            @Schema(description = "날짜별 D-Day 정보 리스트") List<DdayInfo> dDays,

            @Schema(description = "조직명") String organizationName,

            @Schema(description = "전체 지원자 수", example = "42") Long totalApplicants,
            @Schema(description = "파트별 지원서 개수") List<PositionCount> positionCounts
    ) {
        public static SummaryForHome from(Recruitment recruitment, List<PositionCount> positionCounts) {

            List<LocalDate> dates = recruitment.getAvailableTimeRanges().stream()
                    .map(AvailableTimeRange::getDate)
                    .distinct()
                    .sorted()
                    .toList();
            LocalDate interviewStartDate = dates.isEmpty() ? null : dates.get(0);
            LocalDate interviewEndDate   = dates.isEmpty() ? null : dates.get(dates.size() - 1);

            LocalDate today = LocalDate.now();

            List<DdayInfo> ddays = List.of(
                    DdayInfo.of("서류 마감", recruitment.getDocumentDeadline(), today),
                    DdayInfo.of("서류 발표", recruitment.getDocumentResultDate(), today),
                    DdayInfo.of("면접 시작", interviewStartDate, today),
                    DdayInfo.of("면접 종료", interviewEndDate, today),
                    DdayInfo.of("최종 발표", recruitment.getFinalResultDate(), today)
            );

            Long totalApplicants = positionCounts.stream()
                    .mapToLong(PositionCount::count)
                    .sum();

            return new SummaryForHome(
                    recruitment.getId(),
                    recruitment.getTitle(),

                    ddays,

                    recruitment.getOrganization().getName(),

                    totalApplicants,
                    positionCounts
            );
        }
    }

    @Schema(description = "파트명 및 해당 파트 지원서 수")
    public record PositionCount(
            @Schema(description = "파트명") String positionName,
            @Schema(description = "지원서 개수", example = "5") Long count
    ) {}

    @Schema(description = "단일 날짜의 D-Day 정보")
    public record DdayInfo(
            @Schema(description = "이벤트명", example = "서류 마감") String label,
            @Schema(description = "해당 날짜") @DateFormatSlash LocalDate date,
            @Schema(description = "남은 일수 (음수면 이미 지남)", example = "3") Long daysRemaining,
            @Schema(description = "이미 지났는지 여부", example = "false") boolean isPassed
    ) {
        public static DdayInfo of(String label, LocalDate date, LocalDate today) {
            if (date == null) {
                return new DdayInfo(label, null, 0L, false);
            }
            long diff = ChronoUnit.DAYS.between(today, date);
            return new DdayInfo(label, date, diff, diff < 0);
        }
    }

    @Schema(description = "파트별 업무 진행 현황")
    public record TaskProgress(
            @Schema(description = "지원 파트명") String positionName,
            @Schema(description = "D-Day(남은 일수)", example = "3") Long daysToDeadline,
            @Schema(description = "평가해야 할 지원서 수", example = "12") Long totalToEvaluate,
            @Schema(description = "완료된 지원서 수", example = "4") Long evaluatedCount,
            @Schema(description = "평가되지 않은 지원서 수", example = "8") Long notEvaluatedCount,
            @Schema(description = "진행률(%)", example = "33") int progressPercent
    ) {
        public static TaskProgress from(String positionName, Long daysToDeadline, Long totalToEvaluate, Long evaluatedCount, Long notEvaluatedCount, int progressPercent) {
            return new TaskProgress(
                    positionName,
                    daysToDeadline,
                    totalToEvaluate,
                    evaluatedCount,
                    notEvaluatedCount,
                    progressPercent
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

    @Schema(description="파트별 평가 미완료 사용자 리스트 DTO")
    public record PendingEvaluatorByPosition(
            @Schema(description="파트명") String positionName,
            @Schema(description="평가 미완료 사용자 리스트") List<UserResponseDTO.Summary> users
    ) {
        public static PendingEvaluatorByPosition from(String positionName, List<UserResponseDTO.Summary> users) {
            return new PendingEvaluatorByPosition(
                    positionName,
                    users
            );
        }
    }

    @Schema(description="파트별 평가 미완료 사용자 리스트 DTO")
    public record PendingEvaluator(
            @Schema(description = "평가 단계", example = "DOCUMENT") EvaluationType stage,
            @Schema(description = "평가 마감일시") @DateFormatSlash LocalDateTime deadline,
            @Schema(description = "마감까지 남은 일수", example = "3") long daysToDeadline,
            @Schema(description = "마감까지 남은 시간(일 제외)", example = "5") long hoursToDeadline,
            @Schema(description = "마감까지 남은 분(시간 제외)", example = "12") long minutesToDeadline,
            @Schema(description="평가 미완료 사용자 리스트") List<UserResponseDTO.Summary> users
    ) {
        public static PendingEvaluator from(EvaluationType stage, LocalDateTime deadline, long daysToDeadline, long hoursToDeadline, long minutesToDeadline, List<UserResponseDTO.Summary> users) {
            return new PendingEvaluator(
                    stage,
                    deadline,
                    daysToDeadline,
                    hoursToDeadline,
                    minutesToDeadline,
                    users
            );
        }
    }

    @Schema(description = "내 서류 평가 현황 응답 DTO")
    public record MyDocumentEvaluation(
            @Schema(description = "미완료된 지원서 리스트") List<ApplicationResponseDTO.Summary> pending,
            @Schema(description = "완료된 지원서 리스트") List<ApplicationResponseDTO.Summary> done
    ) {
        public static MyDocumentEvaluation from(List<ApplicationResponseDTO.Summary> pending, List<ApplicationResponseDTO.Summary> done) {
            return new MyDocumentEvaluation(
                    pending,
                    done
            );
        }
    }

}
