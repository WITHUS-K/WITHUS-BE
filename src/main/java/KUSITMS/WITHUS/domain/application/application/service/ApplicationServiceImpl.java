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
import KUSITMS.WITHUS.domain.application.applicationOrganizationRole.entity.ApplicationOrganizationRole;
import KUSITMS.WITHUS.domain.application.distributionRequest.dto.DistributionRequestResponseDTO;
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
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.organization.organizationRole.repository.OrganizationRoleRepository;
import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.entity.OrganizationRoleGroup;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.infra.email.sender.MailSender;
import KUSITMS.WITHUS.global.infra.email.template.MailTemplateProvider;
import KUSITMS.WITHUS.global.infra.email.template.MailTemplateType;
import KUSITMS.WITHUS.global.infra.upload.dto.FileResponseDTO;
import KUSITMS.WITHUS.global.infra.upload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicantAvailabilityRepository applicantAvailabilityRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final OrganizationRoleRepository organizationRoleRepository;
    private final EvaluationRepository evaluationRepository;
    private final EvaluationCriteriaRepository evaluationCriteriaRepository;
    private final DocumentQuestionRepository documentQuestionRepository;
    private final ApplicationAnswerRepository applicationAnswerRepository;
    private final UserRepository userRepository;
    private final ApplicationAcquaintanceRepository applicationAcquaintanceRepository;
    private final DistributionRequestRepository distributionRequestRepository;
    private final FileUploadService fileUploadService;
    private final MailSender mailSender;
    private final MailTemplateProvider templateProvider;

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
        List<OrganizationRole> selectedRoles = resolveSelectedRoles(request, recruitment);
        OrganizationRole organizationRole = chooseRepresentativeRole(selectedRoles);

        validator.validateRequiredFields(recruitment, request, profileImage);

        Application application = factory.createApplication(request, recruitment, organizationRole);
        selectedRoles.forEach(role -> application.addApplicationOrganizationRole(ApplicationOrganizationRole.of(application, role)));
        applicationRepository.save(application);

        FileResponseDTO.Upload uploadData = fileUploadService.uploadProfileImage(profileImage,
                recruitment.getOrganization().getId(), recruitment.getId(), application.getId());
        if (uploadData != null) {
            application.updateImageUrl(uploadData.url());
        }

        saveApplicantAvailabilities(application, request.availableTimes());

        List<MultipartFile> fileList = files != null ? files : List.of();
        List<DocumentQuestion> questions = documentQuestionRepository.findCommonAndByOrganizationRoles(recruitment, selectedRoles);
        validator.validateFileAnswers(request.answers(), fileList, questions);

        Map<String, FileResponseDTO.Upload> uploadedFileUrls = fileUploadService.uploadAnswerFiles(fileList,
                recruitment.getOrganization().getId(), recruitment.getId(), application.getId());

        Map<Long, DocumentQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(DocumentQuestion::getId, q -> q));

        List<ApplicationAnswer> answers = factory.createAnswers(application, request.answers(), questionMap, uploadedFileUrls);
        applicationAnswerRepository.saveAll(answers);

        String organizationName = recruitment.getOrganization().getName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)", Locale.KOREAN);

        List<String> interviewDateStrings = recruitment.getAvailableTimeRanges().stream()
                .map(range -> range.getDate().format(formatter))
                .toList();
        String interviewDates = String.join("<br/>", interviewDateStrings);

        // TODO : N+1 발생하나, 추후 Role도 User가 아닌 UserOrganization 단으로 넣어야 더 맞을 것 같고 해서 일단 둠 ..
        String adminEmail = recruitment.getOrganization()
                .getUserOrganizations().stream()
                .map(UserOrganization::getUser)
                .filter(user -> user.getRole() == Role.ADMIN)
                .map(User::getEmail)
                .findFirst()
                .orElse("");

        Map<String, String> variables = Map.of(
                "organizationName", organizationName,
                "documentResultDate", recruitment.getDocumentResultDate() != null
                        ? recruitment.getDocumentResultDate().format(formatter)
                        : "",
                "finalResultDate", recruitment.getFinalResultDate() != null
                        ? recruitment.getFinalResultDate().format(formatter)
                        : "",
                "interviewDates", interviewDates,
                "adminEmail", adminEmail
        );

        String subject = "[" + organizationName + "] 지원서 접수 확인 안내";
        String html = templateProvider.loadTemplate(MailTemplateType.APPLY_SUCCESS, variables);
        mailSender.send(request.email(), subject, html);

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
        Set<Long> currentUserRoleIds = currentUserRoleIds(currentUserId);

        Long recruitmentId = app.getRecruitment().getId();
        Long previousId = applicationRepository.findPreviousIdInRecruitment(recruitmentId, id);
        Long nextId     = applicationRepository.findNextIdInRecruitment(recruitmentId, id);

        List<ApplicantAvailability> availabilityList =
                applicantAvailabilityRepository.findByApplicationId(id);
        List<Evaluation> evaluationList =
                evaluationRepository.findEvaluationsForApplication(id);

        List<EvaluationCriteria> criteriaList = findDocumentCriteriaForCurrentUser(app, currentUserRoleIds);

        return assembler.toDetail(
                app,
                availabilityList,
                evaluationList,
                criteriaList,
                currentUserId,
                previousId,
                nextId
        );
    }

    /**
     * 특정 공고의 로그인한 사용자가 서류 평가 담당자로 지정된 지원서 전체 조회
     * @param recruitmentId 조회할 공고의 ID
     * @return 조회한 공고의 지원서 전체의 정보
     */
    @Override
    public Page<ApplicationResponseDTO.SummaryForUser> getByRecruitmentId(Long recruitmentId, Long currentUserId, EvaluationStatus evaluationStatus, String keyword, Pageable pageable) {
        Set<Long> currentUserRoleIds = currentUserRoleIds(currentUserId);
        List<Application> assignedApps = applicationRepository.findDistinctByRecruitment_IdAndEvaluators_Evaluator_IdAndEvaluators_EvaluationType(recruitmentId, currentUserId, EvaluationType.DOCUMENT);
        List<Application> roleMatchedApps = applicationRepository.findByRecruitmentIdAndNameOrEmail(recruitmentId, null).stream()
                .filter(app -> hasAnySelectedRole(app, currentUserRoleIds))
                .toList();

        Map<Long, Application> appsById = new LinkedHashMap<>();
        assignedApps.forEach(app -> appsById.put(app.getId(), app));
        roleMatchedApps.forEach(app -> appsById.putIfAbsent(app.getId(), app));

        List<ApplicationResponseDTO.SummaryForUser> filtered = appsById.values().stream()
                .map(app -> ApplicationResponseDTO.SummaryForUser.from(app, currentUserId, currentUserRoleIds))
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
    public ApplicationResponseDTO.AdminPageWithStageCounts getByRecruitmentIdForAdmin(
            Long recruitmentId,
            AdminStageFilter stage,
            Pageable pageable,
            AdminApplicationSortField sortBy,
            Sort.Direction direction,
            List<Long> organizationRoleIds,
            List<ApplicationStatus> statuses,
            String keyword
    ) {
        List<Application> allApps = applicationRepository
                .findByRecruitmentIdAndStatusIn(recruitmentId, stage.toStatusList());

        // POSITION_NAME 필터
        if (organizationRoleIds != null && !organizationRoleIds.isEmpty()) {
            allApps = allApps.stream()
                    .filter(app -> hasAnySelectedRole(app, organizationRoleIds))
                    .collect(Collectors.toList());
        }

        // STATUS 필터
        if (statuses != null && !statuses.isEmpty()) {
            allApps = allApps.stream()
                    .filter(app -> statuses.contains(app.getStatus()))
                    .collect(Collectors.toList());
        }

        // NAME KEYWORD 필터
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lower = keyword.trim().toLowerCase(Locale.ROOT);
            allApps = allApps.stream()
                    .filter(app ->
                            app.getName() != null &&
                                    app.getName().toLowerCase().contains(lower)
                    )
                    .collect(Collectors.toList());
        }

        List<Application> sortedApps = getSortedApps(sortBy, direction, allApps);

        List<ApplicationResponseDTO.SummaryForAdmin> allDtos = IntStream.range(0, sortedApps.size())
                .mapToObj(i -> ApplicationResponseDTO.SummaryForAdmin.from(sortedApps.get(i), i + 1L))
                .toList();

        int start = (int) pageable.getOffset();
        int end   = Math.min(start + pageable.getPageSize(), allDtos.size());
        List<ApplicationResponseDTO.SummaryForAdmin> content = start > end
                ? List.of()
                : allDtos.subList(start, end);

        Page<ApplicationResponseDTO.SummaryForAdmin> page = new PageImpl<>(content, pageable, allDtos.size());

        long documentCnt = applicationRepository.countByRecruitmentIdAndStatusIn(
                recruitmentId, AdminStageFilter.DOCUMENT.toStatusList());
        long interviewCnt = applicationRepository.countByRecruitmentIdAndStatusIn(
                recruitmentId, AdminStageFilter.INTERVIEW.toStatusList());
        long finalPassCnt = applicationRepository.countByRecruitmentIdAndStatusIn(
                recruitmentId, AdminStageFilter.FINAL_PASS.toStatusList());
        long failCnt = applicationRepository.countByRecruitmentIdAndStatusIn(
                recruitmentId, AdminStageFilter.FAIL.toStatusList());

        ApplicationResponseDTO.StageCount counts = ApplicationResponseDTO.StageCount.from(
                documentCnt, interviewCnt, finalPassCnt, failCnt
        );

        return ApplicationResponseDTO.AdminPageWithStageCounts.from(page, counts);
    }

    @NotNull
    private static List<Application> getSortedApps(AdminApplicationSortField sortBy, Sort.Direction direction, List<Application> allApps) {

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
                    cmp = selectedRoleSortKey(a).compareToIgnoreCase(selectedRoleSortKey(b));
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
                case LATEST:
                    cmp = a.getCreatedAt().compareTo(b.getCreatedAt());
                    break;
                case NAME:
                default:
                    cmp = sa.name().compareToIgnoreCase(sb.name());
                    break;
            }
            return direction.isDescending() ? -cmp : cmp;
        });
        return allApps;
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
            ApplicationStatus newStatus = mapToRealStatus(request.stage(), request.status());
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
    public DistributionRequestResponseDTO.Detail distributeEvaluatorsLatestRequest(Long recruitmentId) {
        DistributionRequest latest = distributionRequestRepository.findTopByRecruitmentIdOrderByCreatedAtDesc(recruitmentId);

        if (latest == null) {
            return DistributionRequestResponseDTO.Detail.empty(recruitmentId);
        }

        return DistributionRequestResponseDTO.Detail.from(latest);
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

    @Override
    public List<ApplicationResponseDTO.CandidateDTO> findTimeslotCandidates(
            Long recruitmentId,
            Long timeslotId,
            String query,
            boolean excludeCurrent
    ) {
        String q = (query == null || query.isBlank()) ? null : query.trim();
        return applicationRepository.findEligibleCandidates(recruitmentId, timeslotId, q, excludeCurrent);
    }

    /**
     * 메일/문자 발송용 지원자 검색
     * @param recruitmentId 공고 ID
     * @param keyword 검색어 (지원자 이름 또는 이메일)
     * @return 지원자 목록 (지원서 ID, 이름, 이메일, 프로필 사진 URL)
     */
    @Override
    public List<ApplicationResponseDTO.ApplicantForMailSms> searchApplicantsForMailSms(Long recruitmentId, String keyword) {
        List<Application> applications = applicationRepository.findByRecruitmentIdAndNameOrEmail(recruitmentId, keyword);
        return applications.stream()
                .map(ApplicationResponseDTO.ApplicantForMailSms::from)
                .toList();
    }

    @Override
    public List<ApplicationResponseDTO.Detail> getAllDetailForExcel(
            Long recruitmentId,
            AdminStageFilter stage,
            AdminApplicationSortField sortBy,
            Sort.Direction direction,
            List<Long> organizationRoleIds,
            List<ApplicationStatus> statuses,
            String keyword,
            Long currentUserId
    ) {

        List<Application> apps = applicationRepository.findByRecruitmentIdAndStatusIn(
                recruitmentId, stage.toStatusList()
        );

        // POSITION(OrganizationRole) 필터
        if (organizationRoleIds != null && !organizationRoleIds.isEmpty()) {
            apps = apps.stream()
                    .filter(app -> hasAnySelectedRole(app, organizationRoleIds))
                    .collect(Collectors.toList());
        }

        // STATUS 필터
        if (statuses != null && !statuses.isEmpty()) {
            apps = apps.stream()
                    .filter(a -> statuses.contains(a.getStatus()))
                    .collect(Collectors.toList());
        }

        // KEYWORD (name 검색)
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase(Locale.ROOT);
            apps = apps.stream()
                    .filter(a -> a.getName() != null && a.getName().toLowerCase().contains(kw))
                    .collect(Collectors.toList());
        }

        // SORT
        apps = getSortedApps(sortBy, direction, apps);

        if (apps.isEmpty()) {
            return List.of();
        }

        // 성능 최적화 - bulk 조회
        List<Long> appIds = apps.stream()
                .map(Application::getId)
                .toList();

        // 면접 가능 시간 전체 조회
        List<ApplicantAvailability> allAvail =
                applicantAvailabilityRepository.findAllByApplicationIdIn(appIds);

        // 평가 전체 조회
        List<Evaluation> allEvaluations =
                evaluationRepository.findAllByApplicationIdIn(appIds);

        // 평가 기준 (기존 단건 상세 방식과 동일)
        // 단건에서는 DOCUMENT 기준만 가져오지만, Detail.from() 내부에서 인터뷰/서류 모두 사용하므로 recruitment.getEvaluationCriteriaList() 그대로 써도 됨.
        Recruitment recruitment = apps.isEmpty() ? null : apps.get(0).getRecruitment();
        List<EvaluationCriteria> criteriaList =
                recruitment != null ? recruitment.getEvaluationCriteriaList() : List.of();


        // previous, next → 엑셀에서는 불필요하므로 null
        Long previous = null;
        Long next = null;


        // Detail DTO 변환
        Map<Long, List<ApplicantAvailability>> availMap =
                allAvail.stream().collect(Collectors.groupingBy(a -> a.getApplication().getId()));

        Map<Long, List<Evaluation>> evalMap =
                allEvaluations.stream().collect(Collectors.groupingBy(e -> e.getApplication().getId()));


        return apps.stream()
                .map(app -> assembler.toDetail(
                        app,
                        availMap.getOrDefault(app.getId(), List.of()),
                        evalMap.getOrDefault(app.getId(), List.of()),
                        criteriaList,
                        currentUserId,
                        previous,
                        next
                ))
                .toList();
    }


    /**
     * PASS/FAIL/HOLD의 간단 상태를 단계와 현재 상태에 맞춰 ApplicationStatus으로 매핑
     * @param stage   변경할 단계 (DOCUMENT, INTERVIEW, FINAL_PASS, FAIL)
     * @param simple  간단 상태 (PASS, FAIL, HOLD)
     */
    private ApplicationStatus mapToRealStatus(
            AdminStageFilter stage,
            SimpleApplicationStatus simple) {

        boolean isPass = (simple == SimpleApplicationStatus.PASS);

        switch (stage) {
            case DOCUMENT:
                if (simple == SimpleApplicationStatus.HOLD) {
                    return ApplicationStatus.DOX_PENDING;
                }
                return isPass
                        ? ApplicationStatus.DOX_PASS
                        : ApplicationStatus.DOX_FAIL;

            case INTERVIEW:
                if (simple == SimpleApplicationStatus.HOLD) {
                    return ApplicationStatus.INTERVIEW_PENDING;
                }
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

    private List<OrganizationRole> resolveSelectedRoles(ApplicationRequestDTO.Create request, Recruitment recruitment) {
        List<Long> requestedRoleIds = normalizeRequestedRoleIds(request);
        if (requestedRoleIds.isEmpty()) {
            return List.of();
        }

        List<OrganizationRole> selectedRoles = organizationRoleRepository.findAllById(requestedRoleIds);
        if (selectedRoles.size() != requestedRoleIds.size()) {
            throw new CustomException(ErrorCode.ORGANIZATION_ROLE_NOT_EXIST);
        }

        validateRolesBelongToRecruitmentOrganization(selectedRoles, recruitment);
        validateRolesIncludedInRecruitment(selectedRoles, recruitment);
        validateRoleGroupSelection(selectedRoles, recruitment);

        return selectedRoles;
    }

    private List<Long> normalizeRequestedRoleIds(ApplicationRequestDTO.Create request) {
        boolean hasPositionId = request.positionId() != null;
        boolean hasPositionIds = request.positionIds() != null && !request.positionIds().isEmpty();

        if (hasPositionId && hasPositionIds) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        List<Long> roleIds = hasPositionIds ? request.positionIds() : hasPositionId ? List.of(request.positionId()) : List.of();
        List<Long> distinctRoleIds = roleIds.stream().distinct().toList();
        if (distinctRoleIds.size() != roleIds.size()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        return distinctRoleIds;
    }

    private void validateRolesBelongToRecruitmentOrganization(List<OrganizationRole> selectedRoles, Recruitment recruitment) {
        boolean hasOtherOrganizationRole = selectedRoles.stream()
                .anyMatch(role -> !role.getOrganization().getId().equals(recruitment.getOrganization().getId()));

        if (hasOtherOrganizationRole) {
            throw new CustomException(ErrorCode.ORGANIZATION_ROLE_NOT_EXIST);
        }
    }

    private void validateRolesIncludedInRecruitment(List<OrganizationRole> selectedRoles, Recruitment recruitment) {
        Set<Long> recruitmentRoleIds = recruitment.getPositions().stream()
                .map(ror -> ror.getOrganizationRole().getId())
                .collect(Collectors.toSet());

        boolean hasRoleNotInRecruitment = selectedRoles.stream()
                .map(OrganizationRole::getId)
                .anyMatch(roleId -> !recruitmentRoleIds.contains(roleId));

        if (hasRoleNotInRecruitment) {
            throw new CustomException(ErrorCode.ORGANIZATION_ROLE_NOT_EXIST);
        }
    }

    private void validateRoleGroupSelection(List<OrganizationRole> selectedRoles, Recruitment recruitment) {
        Map<Long, OrganizationRoleGroup> recruitmentGroups = recruitment.getPositions().stream()
                .map(ror -> ror.getOrganizationRole().getOrganizationRoleGroup())
                .filter(group -> group != null)
                .collect(Collectors.toMap(
                        OrganizationRoleGroup::getId,
                        group -> group,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        if (recruitmentGroups.isEmpty()) {
            return;
        }

        Map<Long, Long> selectedCountByGroupId = selectedRoles.stream()
                .map(OrganizationRole::getOrganizationRoleGroup)
                .filter(group -> group != null)
                .collect(Collectors.groupingBy(
                        OrganizationRoleGroup::getId,
                        Collectors.counting()
                ));

        for (OrganizationRoleGroup group : recruitmentGroups.values()) {
            long selectedCount = selectedCountByGroupId.getOrDefault(group.getId(), 0L);
            if (selectedCount < group.getSelectionMinCount() || selectedCount > group.getSelectionMaxCount()) {
                throw new CustomException(ErrorCode.INVALID_REQUEST);
            }
        }
    }

    private OrganizationRole chooseRepresentativeRole(List<OrganizationRole> selectedRoles) {
        if (selectedRoles.isEmpty()) {
            return null;
        }

        return selectedRoles.stream()
                .filter(role -> role.getOrganizationRoleGroup() != null)
                .filter(role -> role.getOrganizationRoleGroup().getName().contains("일반"))
                .findFirst()
                .orElse(selectedRoles.get(0));
    }

    private static boolean hasAnySelectedRole(Application application, List<Long> organizationRoleIds) {
        List<Long> selectedRoleIds = selectedRoleIds(application);
        return selectedRoleIds.stream().anyMatch(organizationRoleIds::contains);
    }

    private static boolean hasAnySelectedRole(Application application, Set<Long> organizationRoleIds) {
        List<Long> selectedRoleIds = selectedRoleIds(application);
        return selectedRoleIds.stream().anyMatch(organizationRoleIds::contains);
    }

    private static List<Long> selectedRoleIds(Application application) {
        List<Long> selectedRoleIds = application.getApplicationOrganizationRoles().stream()
                .map(link -> link.getOrganizationRole().getId())
                .toList();

        if (!selectedRoleIds.isEmpty()) {
            return selectedRoleIds;
        }

        return application.getOrganizationRole() == null
                ? List.of()
                : List.of(application.getOrganizationRole().getId());
    }

    private static String selectedRoleSortKey(Application application) {
        List<String> selectedRoleNames = application.getApplicationOrganizationRoles().stream()
                .map(link -> link.getOrganizationRole().getName())
                .sorted(String::compareToIgnoreCase)
                .toList();

        if (!selectedRoleNames.isEmpty()) {
            return String.join(" / ", selectedRoleNames);
        }

        return application.getOrganizationRole() == null ? "" : application.getOrganizationRole().getName();
    }

    private Set<Long> currentUserRoleIds(Long currentUserId) {
        return userRepository.getById(currentUserId).getUserOrganizationRoles().stream()
                .map(userOrganizationRole -> userOrganizationRole.getOrganizationRole().getId())
                .collect(Collectors.toSet());
    }

    private List<EvaluationCriteria> findDocumentCriteriaForCurrentUser(Application app, Set<Long> currentUserRoleIds) {
        Set<Long> selectedRoleIds = Set.copyOf(selectedRoleIds(app));
        Set<Long> matchedRoleIds = selectedRoleIds.stream()
                .filter(currentUserRoleIds::contains)
                .collect(Collectors.toSet());
        Set<Long> targetRoleIds = matchedRoleIds.isEmpty() ? selectedRoleIds : matchedRoleIds;

        return app.getRecruitment().getEvaluationCriteriaList().stream()
                .filter(criteria -> criteria.getEvaluationType() == EvaluationType.DOCUMENT)
                .filter(criteria -> {
                    OrganizationRole criteriaRole = criteria.getOrganizationRole();
                    return criteriaRole == null || targetRoleIds.contains(criteriaRole.getId());
                })
                .toList();
    }

    private boolean matchStatus(ApplicationResponseDTO.SummaryForUser dto, EvaluationStatus status) {
        return switch (status) {
            case EVALUATED -> dto.documentEvaluated();
            case NOT_EVALUATED -> !dto.documentEvaluated();
            case ALL -> true;
        };
    }

    private boolean matchKeyword(ApplicationResponseDTO.SummaryForUser dto, String keyword) {
        return keyword == null || keyword.isBlank() || dto.name().toLowerCase().contains(keyword.toLowerCase());
    }
}
