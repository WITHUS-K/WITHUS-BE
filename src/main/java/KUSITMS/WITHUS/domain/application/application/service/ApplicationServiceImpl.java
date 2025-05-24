package KUSITMS.WITHUS.domain.application.application.service;

import KUSITMS.WITHUS.domain.application.applicantAvailability.entity.ApplicantAvailability;
import KUSITMS.WITHUS.domain.application.applicantAvailability.repository.ApplicantAvailabilityRepository;
import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.enumerate.AdminApplicationSortField;
import KUSITMS.WITHUS.domain.application.application.enumerate.AdminStageFilter;
import KUSITMS.WITHUS.domain.application.application.enumerate.EvaluationStatus;
import KUSITMS.WITHUS.domain.application.application.enumerate.SimpleApplicationStatus;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.application.application.service.assembler.ApplicationAssembler;
import KUSITMS.WITHUS.domain.application.application.service.evaluator.EvaluatorAssignmentService;
import KUSITMS.WITHUS.domain.application.application.service.factory.ApplicationFactory;
import KUSITMS.WITHUS.domain.application.application.service.validator.ApplicationValidator;
import KUSITMS.WITHUS.domain.application.applicationAcquaintance.entity.ApplicationAcquaintance;
import KUSITMS.WITHUS.domain.application.applicationAcquaintance.repository.ApplicationAcquaintanceRepository;
import KUSITMS.WITHUS.domain.application.applicationAnswer.entity.ApplicationAnswer;
import KUSITMS.WITHUS.domain.application.applicationAnswer.repository.ApplicationAnswerRepository;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.dto.ApplicationEvaluatorRequestDTO;
import KUSITMS.WITHUS.domain.application.distributionRequest.entity.DistributionRequest;
import KUSITMS.WITHUS.domain.application.distributionRequest.repository.DistributionRequestRepository;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.evaluation.evaluation.repository.EvaluationRepository;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.repository.EvaluationCriteriaRepository;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.repository.DocumentQuestionRepository;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.position.repository.PositionRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.infra.upload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicantAvailabilityRepository applicantAvailabilityRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final PositionRepository positionRepository;
    private final EvaluationRepository evaluationRepository;
    private final EvaluationCriteriaRepository evaluationCriteriaRepository;
    private final DocumentQuestionRepository documentQuestionRepository;
    private final ApplicationAnswerRepository applicationAnswerRepository;
    private final UserRepository userRepository;
    private final ApplicationAcquaintanceRepository applicationAcquaintanceRepository;
    private final DistributionRequestRepository distributionRequestRepository;
    private final FileUploadService fileUploadService;

    private final ApplicationValidator validator;
    private final ApplicationFactory factory;
    private final EvaluatorAssignmentService evaluatorService;
    private final ApplicationAssembler assembler;

    /**
     * 지원서 생성
     * @param request 지원서 생성 요청 DTO
     * @return 생성된 지원서 정보
     */
    @Override
    @Transactional
    public ApplicationResponseDTO.Summary create(ApplicationRequestDTO.Create request, MultipartFile profileImage, List<MultipartFile> files) {
        Recruitment recruitment = recruitmentRepository.getById(request.recruitmentId());
        Position position = Optional.ofNullable(request.positionId())
                .flatMap(positionRepository::findById)
                .orElse(null);

        validator.validateRequiredFields(recruitment, request);

        Application application = factory.createApplication(request, recruitment, position);
        applicationRepository.save(application);

        String imageUrl = fileUploadService.uploadProfileImage(profileImage,
                recruitment.getOrganization().getId(), recruitment.getId(), application.getId());
        application.updateImageUrl(imageUrl);

        saveApplicantAvailabilities(application, request.availableTimes());

        List<MultipartFile> fileList = files != null ? files : List.of();
        List<DocumentQuestion> questions = documentQuestionRepository.findByRecruitment(recruitment);
        validator.validateFileAnswers(request.answers(), fileList, questions);

        Map<String, String> uploadedFileUrls = fileUploadService.uploadAnswerFiles(fileList,
                recruitment.getOrganization().getId(), recruitment.getId(), application.getId());

        Map<Long, DocumentQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(DocumentQuestion::getId, q -> q));

        List<ApplicationAnswer> answers = factory.createAnswers(application, request.answers(), questionMap, uploadedFileUrls);
        applicationAnswerRepository.saveAll(answers);

        return ApplicationResponseDTO.Summary.from(application);
    }

    /**
     * 지원서 삭제
     * @param id 삭제할 지원서 ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        applicationRepository.getById(id);
        applicationRepository.delete(id);
    }

    /**
     * ID로 지원서 단건 조회
     * @param id 조회할 지원서 ID
     * @return 조회한 지원서 정보
     */
    @Override
    public ApplicationResponseDTO.Detail getById(Long id, Long currentUserId) {
        Application app = applicationRepository.getById(id);
        List<ApplicantAvailability> availabilityList = applicantAvailabilityRepository.findByApplicationId(id);
        List<Evaluation> evaluationList = evaluationRepository.findEvaluationsForApplication(id);
        List<EvaluationCriteria> criteriaList = evaluationCriteriaRepository.findByTypeAndRecruitment(EvaluationType.DOCUMENT, app.getRecruitment().getId());

        return assembler.toDetail(app, availabilityList, evaluationList, criteriaList, currentUserId);
    }

    /**
     * 특정 공고의 지원서 전체 조회
     * @param recruitmentId 조회할 공고의 ID
     * @return 조회한 공고의 지원서 전체의 정보
     */
    @Override
    public Page<ApplicationResponseDTO.SummaryForUser> getByRecruitmentId(Long recruitmentId, Long currentUserId, EvaluationStatus evaluationStatus, String keyword, Pageable pageable) {
        List<Application> apps = applicationRepository.findByRecruitmentId(recruitmentId);
        List<ApplicationResponseDTO.SummaryForUser> filtered = apps.stream()
                .map(app -> ApplicationResponseDTO.SummaryForUser.from(app, currentUserId))
                .filter(dto -> matchStatus(dto, evaluationStatus) && matchKeyword(dto, keyword))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<ApplicationResponseDTO.SummaryForUser> content = start > end ? List.of() : filtered.subList(start, end);

        return new PageImpl<>(content, pageable, filtered.size());
    }

    /**
     * 관리자용 특정 공고의 지원서 전체 조회
     * @param recruitmentId 조회할 공고의 ID
     * @return 조회한 공고의 지원서 전체의 정보
     */
    @Override
    public Page<ApplicationResponseDTO.SummaryForAdmin> getByRecruitmentIdForAdmin(
            Long recruitmentId,
            AdminStageFilter stage,
            Pageable pageable,
            AdminApplicationSortField sortBy,
            Sort.Direction direction
    ) {
        List<Application> allApps = applicationRepository
                .findByRecruitmentIdAndStatusIn(recruitmentId, stage.toStatusList());

        allApps.sort((a, b) -> {
            var sa = ApplicationResponseDTO.SummaryForAdmin.from(a, 0L);
            var sb = ApplicationResponseDTO.SummaryForAdmin.from(b, 0L);

            int cmp;
            switch (sortBy) {
                case DOCUMENT_EVALUATION_STATUS:
                    cmp = Integer.compare(sa.documentEvaluatedCount(), sb.documentEvaluatedCount());
                    break;
                case INTERVIEW_EVALUATION_STATUS:
                    cmp = Integer.compare(sa.interviewEvaluatedCount(), sb.interviewEvaluatedCount());
                    break;
                case DOCUMENT_SCORE:
                    cmp = Double.compare(
                            Double.parseDouble(sa.documentAverageScore()),
                            Double.parseDouble(sb.documentAverageScore())
                    );
                    break;
                case INTERVIEW_SCORE:
                    cmp = Double.compare(
                            Double.parseDouble(sa.interviewAverageScore()),
                            Double.parseDouble(sb.interviewAverageScore())
                    );
                    break;
                case POSITION_NAME:
                    cmp = sa.positionName().compareToIgnoreCase(sb.positionName());
                    break;
                case STATUS:
                    cmp = sa.status().compareTo(sb.status());
                    break;
                case IS_MAIL_SENT: // false < true 순으로 오름차순
                    // Boolean.TRUE.equals 로 null → false 처리
                    boolean mailA = Boolean.TRUE.equals(sa.isMailSent());
                    boolean mailB = Boolean.TRUE.equals(sb.isMailSent());
                    cmp = Boolean.compare(mailA, mailB);
                    break;
                case IS_SMS_SENT:
                    boolean smsA = Boolean.TRUE.equals(sa.isSmsSent());
                    boolean smsB = Boolean.TRUE.equals(sb.isSmsSent());
                    cmp = Boolean.compare(smsA, smsB);
                    break;
                case NAME:
                default:
                    cmp = sa.name().compareToIgnoreCase(sb.name());
                    break;
            }
            return direction.isDescending() ? -cmp : cmp;
        });


        List<ApplicationResponseDTO.SummaryForAdmin> allDtos = IntStream.range(0, allApps.size())
                .mapToObj(i -> ApplicationResponseDTO.SummaryForAdmin.from(allApps.get(i), i + 1L))
                .toList();

        int start = (int) pageable.getOffset();
        int end   = Math.min(start + pageable.getPageSize(), allDtos.size());
        List<ApplicationResponseDTO.SummaryForAdmin> content = start > end
                ? List.of()
                : allDtos.subList(start, end);

        return new PageImpl<>(content, pageable, allDtos.size());
    }


    /**
     * 지원서 ID를 리스트로 받아서 일괄적으로 상태를 변경
     * @param request 일괄 요청 DTO (ID 리스트, 상태값)
     */
    @Override
    @Transactional
    public List<ApplicationResponseDTO.Summary> updateStatus(ApplicationRequestDTO.UpdateStatus request) {
        List<Application> apps = applicationRepository.findAllById(request.applicationIds());
        apps.forEach(app -> {
            ApplicationStatus newStatus = mapToRealStatus(request.stage(), app.getStatus(), request.status());
            app.updateStatus(newStatus);
        });
        return apps.stream()
                .map(ApplicationResponseDTO.Summary::from)
                .toList();
    }


    /**
     * 주어진 공고에 대해 요청된 파트별 정보에 따라 지원서 별 평가자 배정
     * @param request 공고 ID와 함께, 파트별로 평가 담당자 Role ID 및 지원서당 배정할 인원 수를 담은 요청 DTO
     */
    @Override
    @Transactional
    public void distributeEvaluators(ApplicationEvaluatorRequestDTO.Distribute request) {
        evaluatorService.distributeEvaluators(request);
    }

    /**
     * 주어진 공고에 대해 가장 최근 평가자 배정 이력 반환
     * @param recruitmentId 공고 ID
     */
    @Override
    public DistributionRequest distributeEvaluatorsLatestRequest(Long recruitmentId) {
        return distributionRequestRepository.findTopByRecruitmentIdOrderByCreatedAtDesc(recruitmentId);
    }

    /**
     * 주어진 지원서에 대해 기존에 배정된 평가 담당자 임의 재배정
     * @param request applicationId와 새로 배정할 평가자 User ID 리스트를 포함한 요청 DTO
     */
    @Override
    @Transactional
    public void updateEvaluators(ApplicationEvaluatorRequestDTO.Update request) {
        evaluatorService.updateEvaluators(request);
    }

    /**
     * 현재 표기 상태 기반으로 지원서를 지인으로 표시하거나 표시 취소
     * @param applicationId 지인 표시할 지원서 id
     * @param currentUserId 현재 유저의 id
     */
    @Override
    @Transactional
    public boolean toggleAcquaintance(Long applicationId, Long currentUserId) {
        boolean exists = applicationAcquaintanceRepository
                .existsByApplication_IdAndUser_Id(applicationId, currentUserId);

        if (exists) {
            applicationAcquaintanceRepository
                    .deleteByApplication_IdAndUser_Id(applicationId, currentUserId);
            return false;
        } else {
            Application app = applicationRepository.getById(applicationId);
            User user = userRepository.getById(currentUserId);
            applicationAcquaintanceRepository.save(new ApplicationAcquaintance(app, user));
            return true;
        }
    }

    /**
     * PASS/FAIL/HOLD의 간단 상태를 단계와 현재 상태에 맞춰 ApplicationStatus으로 매핑
     * @param stage   변경할 단계 (DOCUMENT, INTERVIEW, FINAL_PASS, FAIL)
     * @param current 현재 ApplicationStatus (PENDING, DOX_PASS, 등)
     * @param simple  간단 상태 (PASS, FAIL, HOLD)
     */
    private ApplicationStatus mapToRealStatus(
            AdminStageFilter stage,
            ApplicationStatus current,
            SimpleApplicationStatus simple) {

        if (simple == SimpleApplicationStatus.HOLD) {
            return ApplicationStatus.PENDING;
        }

        boolean isPass = (simple == SimpleApplicationStatus.PASS);

        switch (stage) {
            case DOCUMENT:
                // 기존 면접 단계였으면 INTERVIEW_
                if (current == ApplicationStatus.INTERVIEW_PASS
                        || current == ApplicationStatus.INTERVIEW_FAIL) {
                    return isPass
                            ? ApplicationStatus.INTERVIEW_PASS
                            : ApplicationStatus.INTERVIEW_FAIL;
                }
                // 그 외(PENDING, DOX_PASS, DOX_FAIL)면 DOX_
                return isPass
                        ? ApplicationStatus.DOX_PASS
                        : ApplicationStatus.DOX_FAIL;

            case INTERVIEW:
                return isPass
                        ? ApplicationStatus.INTERVIEW_PASS
                        : ApplicationStatus.INTERVIEW_FAIL;

            default:
                throw new CustomException(ErrorCode.STAGE_NOT_SUPPORTED);
        }
    }

    private void saveApplicantAvailabilities(Application application, List<LocalDateTime> availableTimes) {
        List<ApplicantAvailability> availabilities = availableTimes.stream()
                .map(time -> ApplicantAvailability.of(application, time))
                .toList();
        applicantAvailabilityRepository.saveAll(availabilities);
    }

    private boolean matchStatus(ApplicationResponseDTO.SummaryForUser dto, EvaluationStatus status) {
        return switch (status) {
            case EVALUATED -> dto.evaluated();
            case NOT_EVALUATED -> !dto.evaluated();
            case ALL -> true;
        };
    }

    private boolean matchKeyword(ApplicationResponseDTO.SummaryForUser dto, String keyword) {
        return keyword == null || keyword.isBlank() || dto.name().toLowerCase().contains(keyword.toLowerCase());
    }
}
