package KUSITMS.WITHUS.domain.application.application.service.assembler;

import KUSITMS.WITHUS.domain.application.applicantAvailability.entity.ApplicantAvailability;
import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.applicationAcquaintance.entity.ApplicationAcquaintance;
import KUSITMS.WITHUS.domain.application.applicationAnswer.dto.ApplicationAnswerResponseDTO;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.entity.ApplicationEvaluator;
import KUSITMS.WITHUS.domain.application.comment.dto.CommentResponseDTO;
import KUSITMS.WITHUS.domain.application.comment.enumerate.CommentType;
import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.interview.interviewQuestion.dto.InterviewQuestionResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.entity.AvailableTimeRange;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ApplicationAssembler {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public ApplicationResponseDTO.Detail toDetail(
            Application application,
            List<ApplicantAvailability> availabilityList,
            List<Evaluation> evaluationList,
            List<EvaluationCriteria> evaluationCriteriaList,
            Long currentUserId
    ) {
        Recruitment recruitment = application.getRecruitment();

        Map<Long, Integer> userScoreMap = evaluationList.stream()
                .filter(e -> e.getUser().getId().equals(currentUserId))
                .collect(Collectors.toMap(
                        e -> e.getCriteria().getId(),
                        Evaluation::getScore
                ));

        return new ApplicationResponseDTO.Detail(
                recruitment.getTitle(),
                recruitment.getDocumentDeadline(),
                recruitment.getDocumentResultDate(),
                recruitment.getFinalResultDate(),
                formatInterviewDates(recruitment),

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

                application.getAnswers().stream().map(ApplicationAnswerResponseDTO::from).toList(),
                availabilityList.stream().map(ApplicantAvailability::getAvailableTime).toList(),
                application.getInterviewQuestions().stream().map(InterviewQuestionResponseDTO.Detail::from).toList(),
                evaluationList.stream().map(EvaluationResponseDTO.Detail::from).toList(),

                getComments(application, CommentType.DOCUMENT),
                getComments(application, CommentType.INTERVIEW),

                recruitment.getDocumentScaleType().getKey(),
                evaluationCriteriaList.stream()
                        .map(c -> EvaluationCriteriaResponseDTO.Detail.from(c, userScoreMap.get(c.getId())))
                        .toList(),

                application.getAcquaintances().stream()
                        .map(ApplicationAcquaintance::getUser)
                        .map(UserResponseDTO.Summary::from)
                        .toList(),
                application.getAcquaintances().size(),

                calculateAverageScore(evaluationList, EvaluationType.DOCUMENT),
                getEvaluatorInfo(evaluationList, EvaluationType.DOCUMENT),
                getPendingEvaluators(application, evaluationList, EvaluationType.DOCUMENT),

                calculateAverageScore(evaluationList, EvaluationType.INTERVIEW),
                getEvaluatorInfo(evaluationList, EvaluationType.INTERVIEW),
                getPendingEvaluators(application, evaluationList, EvaluationType.INTERVIEW)
        );
    }

    private List<String> formatInterviewDates(Recruitment recruitment) {
        return recruitment.getAvailableTimeRanges().stream()
                .map(AvailableTimeRange::getDate)
                .distinct()
                .sorted()
                .map(DATE_FORMATTER::format)
                .toList();
    }

    private List<CommentResponseDTO.Detail> getComments(Application application, CommentType type) {
        return application.getComments().stream()
                .filter(c -> c.getType() == type)
                .map(CommentResponseDTO.Detail::from)
                .toList();
    }

    private String calculateAverageScore(List<Evaluation> evaluations, EvaluationType type) {
        double avg = evaluations.stream()
                .filter(e -> e.getCriteria().getEvaluationType() == type)
                .collect(Collectors.groupingBy(e -> e.getUser().getId()))
                .values().stream()
                .mapToInt(list -> list.stream().mapToInt(Evaluation::getScore).sum())
                .average()
                .orElse(0.0);
        return BigDecimal.valueOf(avg).stripTrailingZeros().toPlainString();
    }

    private List<EvaluationResponseDTO.EvaluatorInfo> getEvaluatorInfo(List<Evaluation> evaluations, EvaluationType type) {
        Map<Long, List<Evaluation>> grouped = evaluations.stream()
                .filter(e -> e.getCriteria().getEvaluationType() == type)
                .collect(Collectors.groupingBy(e -> e.getUser().getId()));

        return grouped.values().stream()
                .map(evaluationList -> {
                    UserResponseDTO.Summary user = UserResponseDTO.Summary.from(evaluationList.get(0).getUser());
                    int total = evaluationList.stream().mapToInt(Evaluation::getScore).sum();
                    return new EvaluationResponseDTO.EvaluatorInfo(user, total);
                })
                .toList();
    }

    private List<UserResponseDTO.Summary> getPendingEvaluators(Application application, List<Evaluation> evaluations, EvaluationType type) {
        Set<Long> evaluatedUserIds = evaluations.stream()
                .filter(e -> e.getCriteria().getEvaluationType() == type)
                .map(e -> e.getUser().getId())
                .collect(Collectors.toSet());

        return application.getEvaluators().stream()
                .filter(ae -> ae.getEvaluationType() == type)
                .map(ApplicationEvaluator::getEvaluator)
                .filter(user -> !evaluatedUserIds.contains(user.getId()))
                .distinct()
                .map(UserResponseDTO.Summary::from)
                .toList();
    }
}
