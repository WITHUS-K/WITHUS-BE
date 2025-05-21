package KUSITMS.WITHUS.domain.application.application.dto;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.enumerate.AcademicStatus;
import KUSITMS.WITHUS.domain.application.applicationAcquaintance.entity.ApplicationAcquaintance;
import KUSITMS.WITHUS.domain.application.applicationAnswer.dto.ApplicationAnswerResponseDTO;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.entity.ApplicationEvaluator;
import KUSITMS.WITHUS.domain.application.availability.entity.ApplicantAvailability;
import KUSITMS.WITHUS.domain.application.comment.dto.CommentResponseDTO;
import KUSITMS.WITHUS.domain.application.comment.entity.Comment;
import KUSITMS.WITHUS.domain.application.comment.enumerate.CommentType;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import KUSITMS.WITHUS.domain.application.interviewQuestion.dto.InterviewQuestionResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.entity.AvailableTimeRange;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.global.common.annotation.DateFormatDot;
import KUSITMS.WITHUS.global.common.annotation.DateFormatSlash;
import KUSITMS.WITHUS.global.common.annotation.TimeFormat;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Schema(description = "지원서 응답 DTO")
public class ApplicationResponseDTO {

    @Schema(description = "지원서 상세 조회 응답 DTO")
    public record Detail(
            @Schema(description = "공고 제목") @JsonFormat(pattern = "2025-2 큐시즘 모집") String title,
            @Schema(description = "지원 마감일") @DateFormatSlash LocalDate documentDeadline,
            @Schema(description = "서류 발표일") @DateFormatSlash LocalDate documentResultDate,
            @Schema(description = "최종 발표일") @DateFormatSlash LocalDate finalResultDate,
            @Schema(description = "면접 일자") List<String> interviewDates,

            @Schema(description = "지원서 ID") Long id,
            @Schema(description = "지원 분야명") String appliedPosition,
            @Schema(description = "지원자 이름") String name,
            @Schema(description = "성별") Gender gender,
            @Schema(description = "이메일") String email,
            @Schema(description = "전화번호") String phoneNumber,
            @Schema(description = "대학명") String university,
            @Schema(description = "전공") String major,
            @Schema(description = "학적 상태") AcademicStatus academicStatus,
            @Schema(description = "생년월일") @DateFormatDot LocalDate birthDate,
            @Schema(description = "이미지 URL") String imageUrl,
            @Schema(description = "주소") String address,
            @Schema(description = "합불 상태") ApplicationStatus status,
            @Schema(description = "지원서 항목 질문 및 답변 목록") List<ApplicationAnswerResponseDTO> documentAnswers,
            @Schema(description = "면접 가능 시간") @TimeFormat List<LocalDateTime> availableTimes,
            @Schema(description = "면접 질문 목록") List<InterviewQuestionResponseDTO.Detail> interviewQuestions,
            @Schema(description = "면접 평가 목록") List<EvaluationResponseDTO.Detail> evaluations,
            @Schema(description = "서류 코맨트 목록") List<CommentResponseDTO.Detail> documentComments,
            @Schema(description = "면접 코맨트 목록") List<CommentResponseDTO.Detail> interviewComments,

            @Schema(description = "서류 평가 방식") String documentScaleTypeKey,
            @Schema(description = "서류 평가 기준 목록") List<EvaluationCriteriaResponseDTO.Detail> documentEvaluationCriterias,

            @Schema(description = "표시된 지인 목록") List<UserResponseDTO.Summary> acquaintances,
            @Schema(description = "지인 수", example = "2") int acquaintanceCount,

            @Schema(description = "서류 평가 평균 점수") String documentAverageScore,
            @Schema(description = "서류 평가 완료한 담당자 리스트") List<EvaluationResponseDTO.EvaluatorInfo> documentCompleted,
            @Schema(description = "서류 평가 미완료 담당자 리스트") List<UserResponseDTO.Summary> documentPending,

            @Schema(description = "면접 평가 평균 점수") String interviewAverageScore,
            @Schema(description = "면접 평가 완료한 담당자 리스트") List<EvaluationResponseDTO.EvaluatorInfo> interviewCompleted,
            @Schema(description = "면접 평가 미완료 담당자 리스트") List<UserResponseDTO.Summary> interviewPending


    ) {
        public static Detail from(Application application, List<ApplicantAvailability> availabilityList, List<Evaluation> evaluationList, List<EvaluationCriteria> evaluationCriteriaList, Long currentUserId) {
            List<ApplicationAnswerResponseDTO> documentAnswers = application.getAnswers().stream()
                    .map(ApplicationAnswerResponseDTO::from)
                    .toList();

            List<LocalDateTime> times = availabilityList.stream()
                    .map(ApplicantAvailability::getAvailableTime)
                    .toList();

            List<InterviewQuestionResponseDTO.Detail> questions = application.getInterviewQuestions().stream()
                    .map(InterviewQuestionResponseDTO.Detail::from)
                    .toList();

            List<EvaluationResponseDTO.Detail> evaluations = evaluationList.stream()
                    .map(EvaluationResponseDTO.Detail::from)
                    .toList();

            List<CommentResponseDTO.Detail> documentComments = application.getComments().stream()
                    .filter(c -> c.getType() == CommentType.DOCUMENT)
                    .map(CommentResponseDTO.Detail::from)
                    .toList();

            List<CommentResponseDTO.Detail> interviewComments = application.getComments().stream()
                    .filter(c -> c.getType() == CommentType.INTERVIEW)
                    .map(CommentResponseDTO.Detail::from)
                    .toList();

            Map<Long, Integer> userScoreMap = evaluationList.stream()
                    .filter(e -> e.getUser().getId().equals(currentUserId))
                    .collect(Collectors.toMap(
                            e -> e.getCriteria().getId(),
                            Evaluation::getScore
                    ));

            List<EvaluationCriteriaResponseDTO.Detail> documentEvaluationCriterias = evaluationCriteriaList.stream()
                    .map(criteria -> {
                        Integer score = userScoreMap.get(criteria.getId()); // 사용자 점수 (null 가능)
                        return EvaluationCriteriaResponseDTO.Detail.from(criteria, score);
                    })
                    .toList();

            Recruitment recruitment = application.getRecruitment();
            String documentScaleTypeKey = recruitment.getDocumentScaleType().getKey();

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            List<String> interviewDates = recruitment.getAvailableTimeRanges().stream()
                    .map(AvailableTimeRange::getDate)
                    .distinct()
                    .sorted()
                    .map(outputFormatter::format)
                    .toList();

            List<UserResponseDTO.Summary> acquaintances = application.getAcquaintances().stream()
                    .map(ApplicationAcquaintance::getUser)
                    .map(UserResponseDTO.Summary::from)
                    .toList();

            // 배정된 서류 평가자 리스트
            List<UserResponseDTO.Summary> assignedDocument = application.getEvaluators().stream()
                    .filter(ae -> ae.getEvaluationType() == EvaluationType.DOCUMENT)
                    .map(ApplicationEvaluator::getEvaluator)
                    .distinct()
                    .map(UserResponseDTO.Summary::from)
                    .toList();

            // 서류 평가만 뽑아서 userId → [Evaluation]
            Map<Long, List<Evaluation>> docByUser = evaluationList.stream()
                    .filter(e -> e.getCriteria().getEvaluationType() == EvaluationType.DOCUMENT)
                    .collect(Collectors.groupingBy(e -> e.getUser().getId()));

            // 완료/미완료 리스트
            List<EvaluationResponseDTO.EvaluatorInfo> docDone = docByUser.entrySet().stream()
                    .map(entry -> {
                        Long userId = entry.getKey();
                        int total = entry.getValue().stream().mapToInt(Evaluation::getScore).sum();
                        UserResponseDTO.Summary summ = UserResponseDTO.Summary.from(
                                entry.getValue().get(0).getUser()
                        );
                        return new EvaluationResponseDTO.EvaluatorInfo(summ, total);
                    })
                    .toList();

            List<UserResponseDTO.Summary> docPending = assignedDocument.stream()
                    .filter(u -> !docByUser.containsKey(u.userId()))
                    .toList();

            // 서류 평균 점수
            double docAvg = docDone.stream().mapToInt(EvaluationResponseDTO.EvaluatorInfo::totalScore).average().orElse(0.0);
            String documentAverageScore = BigDecimal.valueOf(docAvg)
                    .stripTrailingZeros().toPlainString();

            // —— 면접 평가도 똑같이 ——
            List<UserResponseDTO.Summary> assignedInterview = application.getEvaluators().stream()
                    .filter(ae -> ae.getEvaluationType() == EvaluationType.INTERVIEW)
                    .map(ApplicationEvaluator::getEvaluator)
                    .distinct()
                    .map(UserResponseDTO.Summary::from)
                    .toList();

            Map<Long, List<Evaluation>> intByUser = evaluationList.stream()
                    .filter(e -> e.getCriteria().getEvaluationType() == EvaluationType.INTERVIEW)
                    .collect(Collectors.groupingBy(e -> e.getUser().getId()));

            List<EvaluationResponseDTO.EvaluatorInfo> intDone = intByUser.entrySet().stream()
                    .map(entry -> {
                        int total = entry.getValue().stream().mapToInt(Evaluation::getScore).sum();
                        UserResponseDTO.Summary summ = UserResponseDTO.Summary.from(
                                entry.getValue().get(0).getUser()
                        );
                        return new EvaluationResponseDTO.EvaluatorInfo(summ, total);
                    })
                    .toList();

            List<UserResponseDTO.Summary> intPending = assignedInterview.stream()
                    .filter(u -> !intByUser.containsKey(u.userId()))
                    .toList();

            double intAvg = intDone.stream().mapToInt(EvaluationResponseDTO.EvaluatorInfo::totalScore).average().orElse(0.0);
            String interviewAverageScore = BigDecimal.valueOf(intAvg)
                    .stripTrailingZeros().toPlainString();

            return new Detail(
                    recruitment.getTitle(),
                    recruitment.getDocumentDeadline(),
                    recruitment.getDocumentResultDate(),
                    recruitment.getFinalResultDate(),
                    interviewDates,
                    application.getId(),
                    application.getPosition().getName(),
                    application.getName(),
                    application.getGender(),
                    application.getEmail(),
                    application.getPhoneNumber(),
                    application.getUniversity(),
                    application.getMajor(),
                    application.getAcademicStatus(),
                    application.getBirthDate(),
                    application.getImageUrl(),
                    application.getAddress(),
                    application.getStatus(),
                    documentAnswers,
                    times,
                    questions,
                    evaluations,
                    documentComments,
                    interviewComments,
                    documentScaleTypeKey,
                    documentEvaluationCriterias,
                    acquaintances,
                    acquaintances.size(),
                    documentAverageScore,
                    docDone,
                    docPending,
                    interviewAverageScore,
                    intDone,
                    intPending
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

    @Schema(description = "사용자용 지원서 요약 응답 DTO")
    public record SummaryForUser(
            @Schema(description = "서류 발표 여부", example = "true") boolean documentResultAnnounced,
            @Schema(description = "지원서 ID") Long id,
            @Schema(description = "지원자 이름") String name,
            @Schema(description = "파트명") String positionName,
            @Schema(description = "합불 상태") ApplicationStatus status,
            @Schema(description = "해당 평가자가 이 지원서를 평가했는지 여부") boolean evaluated,
            @Schema(description = "이 사용자가 준 총 점수", example = "20", nullable = true) @Nullable Integer myScoreTotal
    ) {
        public static SummaryForUser from(Application application, Long currentUserId) {
            List<Evaluation> userEvaluations = application.getEvaluations().stream()
                    .filter(e -> e.getUser().getId().equals(currentUserId))
                    .toList();

            boolean evaluated = !userEvaluations.isEmpty();

            Integer totalScore = evaluated
                    ? userEvaluations.stream()
                    .mapToInt(Evaluation::getScore)
                    .sum()
                    : null;

            Recruitment recruitment = application.getRecruitment();
            boolean isDocumentResultAnnounced = LocalDate.now().isAfter(recruitment.getDocumentResultDate())
                    || LocalDate.now().isEqual(recruitment.getDocumentResultDate());

            return new SummaryForUser(
                    isDocumentResultAnnounced,
                    application.getId(),
                    application.getName(),
                    application.getPosition() != null ? application.getPosition().getName() : null,
                    application.getStatus(),
                    evaluated,
                    totalScore
            );
        }
    }

    @Schema(description = "관리자용 지원서 요약 응답 DTO")
    public record SummaryForAdmin(
            @Schema(description = "목록 내 순번", example = "001") String sequence,
            @Schema(description = "지원서 ID") Long id,
            @Schema(description = "지원자 이름") String name,
            @Schema(description = "파트명") String positionName,
            @Schema(description = "합불 상태") ApplicationStatus status,

            @Schema(description = "서류 평가 담당자 수", example = "3") int documentAssignedCount,
            @Schema(description = "서류 평가 완료 담당자 수", example = "2") int documentEvaluatedCount,
            @Schema(description = "서류 평가 총점", example = "170") String documentAverageScore,
            @Schema(description = "서류 평가 담당자 리스트") List<UserResponseDTO.Summary> documentEvaluators,

            @Schema(description = "면접 평가 담당자 수", example = "3") int interviewAssignedCount,
            @Schema(description = "면접 평가 완료 담당자 수", example = "1") int interviewEvaluatedCount,
            @Schema(description = "면접 평가 총점", example = "85") String interviewAverageScore,
            @Schema(description = "면접 평가 담당자 리스트") List<UserResponseDTO.Summary> interviewEvaluators,

            @Schema(description = "메일 발송 여부") Boolean isMailSent,
            @Schema(description = "문자 발송 여부") Boolean isSmsSent
    ) {
        public static SummaryForAdmin from(Application application, long sequenceNumber) {
            String seq = String.format("%03d", sequenceNumber);

            // 배정된 평가자 분류
            List<ApplicationEvaluator> evals = application.getEvaluators();
            List<UserResponseDTO.Summary> docAssigned = evals.stream()
                    .filter(ae -> ae.getEvaluationType() == EvaluationType.DOCUMENT)
                    .map(ApplicationEvaluator::getEvaluator)
                    .distinct()
                    .map(UserResponseDTO.Summary::from)
                    .toList();
            List<UserResponseDTO.Summary> intAssigned = evals.stream()
                    .filter(ae -> ae.getEvaluationType() == EvaluationType.INTERVIEW)
                    .map(ApplicationEvaluator::getEvaluator)
                    .distinct()
                    .map(UserResponseDTO.Summary::from)
                    .toList();

            // 실제 평가(Evaluation) 분류
            List<Evaluation> allEvals = application.getEvaluations();
            Map<Long, Integer> docScores = allEvals.stream()
                    .filter(e -> e.getCriteria().getEvaluationType() == EvaluationType.DOCUMENT)
                    .collect(Collectors.groupingBy(
                            e -> e.getUser().getId(),
                            Collectors.summingInt(Evaluation::getScore)
                    ));
            Map<Long, Integer> intScores = allEvals.stream()
                    .filter(e -> e.getCriteria().getEvaluationType() == EvaluationType.INTERVIEW)
                    .collect(Collectors.groupingBy(
                            e -> e.getUser().getId(),
                            Collectors.summingInt(Evaluation::getScore)
                    ));

            // 완료/미완료 계산
            int docAssignedCount     = docAssigned.size();
            int documentEvaluatedCount = docScores.size();
            int interviewAssignedCount = intAssigned.size();
            int interviewEvaluatedCount = intScores.size();

            // 평균 점수 계산 및 문자열 변환
            double docAvg = docScores.values().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
            String documentAverageScore = BigDecimal.valueOf(docAvg)
                    .stripTrailingZeros()
                    .toPlainString();

            double intAvg = intScores.values().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
            String interviewAverageScore = BigDecimal.valueOf(intAvg)
                    .stripTrailingZeros()
                    .toPlainString();

            return new SummaryForAdmin(
                    seq,
                    application.getId(),
                    application.getName(),
                    application.getPosition() != null ? application.getPosition().getName() : null,
                    application.getStatus(),

                    docAssignedCount,
                    documentEvaluatedCount,
                    documentAverageScore,
                    docAssigned,

                    interviewAssignedCount,
                    interviewEvaluatedCount,
                    interviewAverageScore,
                    intAssigned,

                    application.getIsMailSent(),
                    application.getIsSmsSent()
            );
        }
    }

    @Schema(description = "간단한 지원서 응답 DTO")
    public record DetailForTimeSlot(
            @Schema(description = "지원서 ID") Long applicationId,
            @Schema(description = "지원자 이름") String name,
            @Schema(description = "면접 일자") @DateFormatDot LocalDate date,
            @Schema(description = "면접 시작 시간") @TimeFormat LocalTime startTime,
            @Schema(description = "면접 종료 시간") @TimeFormat LocalTime endTime,
            @Schema(description = "상태") ApplicationStatus status,
            @Schema(description = "지원서 항목 질문 및 답변 목록") List<ApplicationAnswerResponseDTO> documentAnswers,
            @Schema(description = "면접 질문 목록") List<InterviewQuestionResponseDTO.Detail> interviewQuestions,
            @Schema(description = "면접 평가 목록") List<EvaluationResponseDTO.Detail> evaluations,
            @Schema(description = "서류 코맨트 목록") List<CommentResponseDTO.Detail> documentComments,
            @Schema(description = "면접 코맨트 목록") List<CommentResponseDTO.Detail> interviewComments
    ) {
        public static DetailForTimeSlot from(
                Application application,
                TimeSlot timeSlot,
                List<Evaluation> evaluationList,
                List<Comment> documentComments,
                List<Comment> interviewComments
        ) {
            List<ApplicationAnswerResponseDTO> documentAnswers = application.getAnswers().stream()
                    .map(ApplicationAnswerResponseDTO::from)
                    .toList();

            List<InterviewQuestionResponseDTO.Detail> questions = application.getInterviewQuestions().stream()
                    .map(InterviewQuestionResponseDTO.Detail::from)
                    .toList();

            List<EvaluationResponseDTO.Detail> evaluations = evaluationList.stream()
                    .map(EvaluationResponseDTO.Detail::from)
                    .toList();

            List<CommentResponseDTO.Detail> documentCommentDTOs = documentComments.stream()
                    .map(CommentResponseDTO.Detail::from)
                    .toList();

            List<CommentResponseDTO.Detail> interviewCommentDTOs = interviewComments.stream()
                    .map(CommentResponseDTO.Detail::from)
                    .toList();

            return new DetailForTimeSlot(
                    application.getId(),
                    application.getName(),
                    timeSlot.getDate(),
                    timeSlot.getStartTime(),
                    timeSlot.getEndTime(),
                    application.getStatus(),
                    documentAnswers,
                    questions,
                    evaluations,
                    documentCommentDTOs,
                    interviewCommentDTOs
            );
        }
    }

}
