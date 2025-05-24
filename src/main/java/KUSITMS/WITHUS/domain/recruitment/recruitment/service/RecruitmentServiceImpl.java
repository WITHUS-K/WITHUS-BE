package KUSITMS.WITHUS.domain.recruitment.recruitment.service;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.entity.ApplicationEvaluator;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.repository.ApplicationEvaluatorRepository;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import KUSITMS.WITHUS.domain.evaluation.evaluation.repository.EvaluationRepository;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.repository.EvaluationCriteriaRepository;
import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.position.repository.PositionRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper.AvailableTimeRangeAppender;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper.DocumentQuestionAppender;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper.EvaluationCriteriaAppender;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper.PositionAppender;
import KUSITMS.WITHUS.domain.recruitment.recruitment.util.SlugGenerator;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.userOrganization.repository.UserOrganizationRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitmentServiceImpl implements RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final OrganizationRepository organizationRepository;
    private final UserOrganizationRepository userOrganizationRepository;
    private final ApplicationRepository applicationRepository;
    private final PositionRepository positionRepository;
    private final EvaluationRepository evaluationRepository;
    private final EvaluationCriteriaRepository evaluationCriteriaRepository;
    private final ApplicationEvaluatorRepository applicationEvaluatorRepository;

    private final AvailableTimeRangeAppender availableTimeRangeAppender;
    private final PositionAppender positionAppender;
    private final DocumentQuestionAppender documentQuestionAppender;
    private final EvaluationCriteriaAppender criteriaAppender;

    private static final int MAX_ATTEMPTS = 10;

    /**
     * 공고 임시 저장
     * @param request 저장 DTO
     * @return 저장된 공고 정보
     */
    @Override
    @Transactional
    public RecruitmentResponseDTO.Create saveDraft(RecruitmentRequestDTO.Upsert request) {
        return saveRecruitment(request, true);
    }

    /**
     * 공고 최종 저장
     * @param request 저장 DTO
     * @return 저장된 공고 정보
     */
    @Override
    @Transactional
    public RecruitmentResponseDTO.Create publish(RecruitmentRequestDTO.Upsert request) {
        return saveRecruitment(request, false);
    }

    /**
     * ID로 공고를 단건 조회
     * @param id 조회할 공고 ID
     * @return 공고 상세 정보 응답
     */
    @Override
    public RecruitmentResponseDTO.Detail getById(Long id) {
        Recruitment recruitment = recruitmentRepository.getById(id);

        return RecruitmentResponseDTO.Detail.from(recruitment);
    }

    /**
     * 주어진 공고 ID로 Recruitment 엔티티를 조회하고, 제공된 mapper 함수를 적용하여 결과를 반환
     * @param id     조회할 공고의 ID
     * @param mapper Recruitment 엔티티를 변환할 함수
     * @param <R>    mapper 함수가 반환하는 타입
     * @return mapper.apply(rec)가 반환하는 변환 결과
     */
    @Override
    public <R> R getByIdAs(Long id, Function<Recruitment,R> mapper) {
        Recruitment rec = recruitmentRepository.getById(id);

        return mapper.apply(rec);
    }

    /**
     * 특정 유저가 속한 조직에서 현재 진행 중인 공고의 요약 정보를 조회
     * @param userId        요청한 유저의 ID
     * @param organizationId 조회할 조직의 ID (유저가 속해 있는 조직이어야 함)
     * @return 해당 조직의 현재 진행 중인 공고 요약 리스트
     */
    @Override
    public List<RecruitmentResponseDTO.SummaryForHome> getCurrentSummariesForUser(Long userId, Long organizationId) {

        boolean isMember = userOrganizationRepository
                .existsByUser_IdAndOrganization_Id(userId, organizationId);
        if (!isMember) {
            throw new CustomException(ErrorCode.USER_ORGANIZATION_NOT_FOUND);
        }

        return findCurrentSummaries(List.of(organizationId));
    }

    /**
     * 관리자로 로그인한 유저가 매핑된 단일 조직에서 현재 진행 중인 공고의 요약 정보를 조회
     * @param adminUserId 관리자 유저의 ID
     * @return 관리자 조직의 현재 진행 중인 공고 요약 리스트
     */
    @Override
    public List<RecruitmentResponseDTO.SummaryForHome> getCurrentSummariesForAdmin(Long adminUserId) {
        // Admin 은 하나의 조직에만 매핑되어 있다고 가정
        Long orgId = userOrganizationRepository.findByUser_Id(adminUserId).stream()
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_EXIST))
                .getOrganization().getId();

        return findCurrentSummaries(List.of(orgId));
    }

    /**
     * 특정 공고의 서류 또는 면접 업무 진행 상황을 파트별로 계산
     * @param recruitmentId 조회할 공고 ID
     * @param stage DOCUMENT 또는 INTERVIEW
     */
    @Override
    public List<RecruitmentResponseDTO.TaskProgress> getTaskProgress(Long recruitmentId, EvaluationType stage) {
        Recruitment recruitment = recruitmentRepository.getById(recruitmentId);
        LocalDate today = LocalDate.now();

        // 마감일(D-Day 계산에 사용할 날짜)
        LocalDate deadline = (stage == EvaluationType.DOCUMENT)
                ? recruitment.getDocumentResultDate()
                : recruitment.getFinalResultDate();
        long daysToDeadline = ChronoUnit.DAYS.between(today, deadline);

        long requiredCriteriaCount = evaluationCriteriaRepository
                .countByRecruitment_IdAndEvaluationType(recruitmentId, stage);

        // 공고에 속한 모든 파트
        List<Position> positions = positionRepository.findByRecruitment_Id(recruitmentId);

        return positions.stream().map(pos -> {
            Long posId = pos.getId();

            // 이 파트의 지원서 전체 조회
            List<Application> apps = applicationRepository.findByRecruitment_IdAndPosition_Id(recruitmentId, posId);

            // stage 에 따라 평가 대상 필터링
            List<Application> targets = apps.stream()
                    .filter(app -> {
                        if (stage == EvaluationType.DOCUMENT) { // DOCUMENT 단계: 전체 지원서가 대상
                            return true;
                        } else { // INTERVIEW 단계: 서류 불합격 지원서는 제외
                            return app.getStatus() != ApplicationStatus.DOX_FAIL;
                        }
                    })
                    .toList();

            long totalToEvaluate = targets.size();

            // 모든 기준 채운(완료된) 지원서 수
            long evaluatedCount = evaluationRepository
                    .countFullyEvaluatedApplications(
                            recruitmentId, posId, stage, requiredCriteriaCount
                    );

            // 아직 평가되지 않은 지원서 수
            long notEvaluatedCount = totalToEvaluate - evaluatedCount;

            // 진행률
            int progressPercent = totalToEvaluate == 0
                    ? 0
                    : (int) (evaluatedCount * 100 / totalToEvaluate);

            return RecruitmentResponseDTO.TaskProgress.from(
                    pos.getName(),
                    daysToDeadline,
                    totalToEvaluate,
                    evaluatedCount,
                    notEvaluatedCount,
                    progressPercent
            );
        }).toList();
    }

    /**
     * 서류/면접 발표일 기준 해당하는 단계의 파트별 평가 미완료 사용자 명단을 반환
     * @param recruitmentId 조회할 공고 ID
     */
    @Override
    public RecruitmentResponseDTO.PendingEvaluator getPendingEvaluators(Long recruitmentId) {
        // 공고 조회
        Recruitment recruitment = recruitmentRepository.getById(recruitmentId);
        LocalDateTime now = LocalDateTime.now();

        // 오늘이 서류 발표일(문서 결과일) 이전 또는 당일인지, 아니면 최종 발표일 이전 또는 당일인지 판단
        EvaluationType stage;
        LocalDateTime deadline;
        if (!now.toLocalDate().isAfter(recruitment.getDocumentResultDate())) { // today ≤ documentResultDate
            stage = EvaluationType.DOCUMENT;
            deadline = recruitment.getDocumentResultDate().atTime(23, 59, 59);
        } else if (!now.toLocalDate().isAfter(recruitment.getFinalResultDate())) { // documentResultDate < today ≤ finalResultDate
            stage = EvaluationType.INTERVIEW;
            deadline = recruitment.getDocumentResultDate().atTime(23, 59, 59);
        } else { // 그 외(최종 발표일 지남)에는 빈 리스트 반환
            return RecruitmentResponseDTO.PendingEvaluator.from(stage = null,
                    deadline = now,
                    0, 0, 0,
                    List.of());
        }

        // 남은 시간 계산
        Duration duration = Duration.between(now, deadline);
        long days    = duration.toDays();
        long hours   = duration.minusDays(days).toHours();
        long minutes = duration.minusDays(days)
                .minusHours(hours)
                .toMinutes();

        // 이 공고/단계의 평가 기준 ID 목록
        List<Long> criteriaIds = evaluationCriteriaRepository
                .findByRecruitment_IdAndEvaluationType(recruitmentId, stage)
                .stream().map(EvaluationCriteria::getId).toList();

        // 파트 별 미완료 평가자 추출
        List<RecruitmentResponseDTO.PendingEvaluatorByPosition> byPosition = positionRepository.findByRecruitment_Id(recruitmentId)
                .stream()
                .map(pos -> {
                    // 이 파트에 배정된 평가자 ⇢ ApplicationEvaluator
                    List<ApplicationEvaluator> assigns = applicationEvaluatorRepository
                            .findByRecruitmentAndPositionAndType(
                                    recruitmentId, pos.getId(), stage
                            );

                    // 평가자별로 “내가 맡은 지원서” 묶기
                    Map<User, List<Application>> appsByUser = assigns.stream()
                            .collect(Collectors.groupingBy(
                                    ApplicationEvaluator::getEvaluator,
                                    Collectors.mapping(ApplicationEvaluator::getApplication, toList())
                            ));

                    // “미완료” 평가자 필터링 (하나라도 기준 개수만큼 평가 안 끝낸 평가자는 “미완료”)
                    List<UserResponseDTO.Summary> pending = appsByUser.entrySet().stream()
                            .filter(entry -> {
                                User user = entry.getKey();
                                // 내가 맡은 지원서 중에 하나라도 “모든 기준” 평가가 안 끝난 게 있으면 미완료
                                return entry.getValue().stream().anyMatch(app ->
                                        evaluationRepository.countByApplication_IdAndUser_IdAndCriteria_IdIn(
                                                app.getId(), user.getId(), criteriaIds
                                        ) < criteriaIds.size()
                                );
                            })
                            .map(e -> UserResponseDTO.Summary.from(e.getKey()))
                            .distinct()
                            .toList();

                    return RecruitmentResponseDTO.PendingEvaluatorByPosition.from(pos.getName(), pending);
                })
                .toList();

        // 파트별 미완료자 수집 - 플랫하게
        List<UserResponseDTO.Summary> pending = byPosition.stream()
                .flatMap(dto -> dto.users().stream())
                .distinct()
                .toList();

        return RecruitmentResponseDTO.PendingEvaluator.from(
                stage,
                deadline,
                days,
                hours,
                minutes,
                pending
        );
    }

    /**
     * 내가 맡은 서류 평가 지원서 중, 미완료/완료 리스트 조회
     * @param userId 현재 평가자 ID
     */
    @Override
    public RecruitmentResponseDTO.MyDocumentEvaluation getMyDocumentEvaluations(Long userId, Long recruitmentId) {
        // 내가 서류 평가자로 할당된 모든 레코드
        List<ApplicationEvaluator> assigns = applicationEvaluatorRepository
                .findByEvaluatorAndRecruitmentAndType(userId, EvaluationType.DOCUMENT, recruitmentId);

        // 공고별로 필요한 문항 개수 캐시
        long needed = evaluationCriteriaRepository
                .countByRecruitment_IdAndEvaluationType(recruitmentId, EvaluationType.DOCUMENT);

        LinkedHashSet<ApplicationResponseDTO.Summary> pending = new LinkedHashSet<>();
        LinkedHashSet<ApplicationResponseDTO.Summary> done    = new LinkedHashSet<>();

        // 각 할당마다 “나(=userId)가 이 지원서에 남긴 평가 개수” 비교
        for (ApplicationEvaluator ae : assigns) {
            Application app = ae.getApplication();

            long answered = evaluationRepository.countByApplication_IdAndUser_IdAndCriteria_EvaluationType(
                    app.getId(), userId, EvaluationType.DOCUMENT
            );

            ApplicationResponseDTO.Summary dto = ApplicationResponseDTO.Summary.from(app);
            if (answered >= needed) done.add(dto);
            else                    pending.add(dto);
        }

        return RecruitmentResponseDTO.MyDocumentEvaluation.from(
                new ArrayList<>(pending),
                new ArrayList<>(done)
        );
    }

    /**
     * 공고 수정
     * @param id 수정할 모집 공고 ID
     * @param request 수정할 정보가 담긴 DTO
     * @return 수정된 모집 공고 응답 DTO
     */
    @Override
    @Transactional
    public RecruitmentResponseDTO.Update update(Long id, RecruitmentRequestDTO.Update request) {
        Recruitment recruitment = recruitmentRepository.getById(id);

        recruitment.update(
                request.title(), request.content(), request.documentDeadline(), request.isDocumentResultRequired(),
                request.documentResultDate(), request.finalResultDate(), request.isInterviewRequired(), request.interviewDuration(),
                request.needGender(), request.needAddress(), request.needSchool(), request.needBirthDate(),
                request.needAcademicStatus(), request.documentScaleType(), request.interviewScaleType()
        );

        if (request.isTemporary()) {
            recruitment.markAsTemporary();
        } else {
            recruitment.markAsFinal();
        }

        return RecruitmentResponseDTO.Update.from(recruitment);
    }

    /**
     * ID로 공고를 삭제
     * @param id 삭제할 공고 ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        recruitmentRepository.getById(id);
        recruitmentRepository.delete(id);
    }

    /**
     * keyword 가 없으면 공고 전체 조회, 있으면 검색
     * @param keyword 공고의 title에서 검색할 내용
     * @return 검색된 공고의 정보 반환
     */
    @Override
    public List<RecruitmentResponseDTO.Summary> getAllByKeyword(String keyword) {
        return recruitmentRepository.findAllByKeyword(keyword).stream()
                .map(RecruitmentResponseDTO.Summary::from)
                .toList();
    }

    @Override
    public RecruitmentResponseDTO.Detail getBySlug(String slug) {
        Recruitment recruitment = recruitmentRepository.findByUrlSlug(slug)
                .orElseThrow(() -> new CustomException(ErrorCode.RECRUITMENT_NOT_EXIST));
        return RecruitmentResponseDTO.Detail.from(recruitment);
    }

    @Override
    public List<RecruitmentResponseDTO.Simple> getAllByUserOrganizations(User user) {
        List<Long> organizationIds = user.getUserOrganizations().stream()
                .map(userOrg -> userOrg.getOrganization().getId())
                .toList();

        List<Recruitment> recruitments = recruitmentRepository.findAllByOrganizationIds(organizationIds);

        return recruitments.stream()
                .map(RecruitmentResponseDTO.Simple::from)
                .toList();
    }

    private RecruitmentResponseDTO.Create saveRecruitment(RecruitmentRequestDTO.Upsert request, boolean isTemporary) {
        Organization organization = organizationRepository.getById(request.organizationId());

        Recruitment recruitment = (request.recruitmentId() != null)
                ? updateRecruitment(request, isTemporary)
                : createRecruitment(request, organization, isTemporary);

        recruitment.clearEvaluationCriteria();
        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);

        positionAppender.append(recruitment, request.positions());
        criteriaAppender.appendWithPositions(recruitment, request.documentEvaluationCriteria(), EvaluationType.DOCUMENT);
        criteriaAppender.appendWithPositions(recruitment, request.interviewEvaluationCriteria(), EvaluationType.INTERVIEW);
        availableTimeRangeAppender.append(recruitment, request.availableTimeRanges());
        documentQuestionAppender.append(recruitment, request.applicationQuestions());

        return RecruitmentResponseDTO.Create.from(savedRecruitment);
    }

    private Recruitment createRecruitment(RecruitmentRequestDTO.Upsert request, Organization organization, boolean isTemporary) {
        return Recruitment.create(
                request.title(), request.content(), request.documentDeadline(), request.isDocumentResultRequired(),
                request.documentResultDate(), request.finalResultDate(), request.isInterviewRequired(), request.interviewDuration(),
                organization, request.needGender(), request.needAddress(), request.needSchool(), request.needBirthDate(),
                request.needAcademicStatus(), isTemporary, request.documentScaleType(), request.interviewScaleType(), generateUniqueSlug()
        );
    }

    private Recruitment updateRecruitment(RecruitmentRequestDTO.Upsert request, boolean isTemporary) {
        Recruitment recruitment = recruitmentRepository.getById(request.recruitmentId());

        recruitment.update(
                request.title(), request.content(), request.documentDeadline(), request.isDocumentResultRequired(),
                request.documentResultDate(), request.finalResultDate(), request.isInterviewRequired(), request.interviewDuration(),
                request.needGender(), request.needAddress(), request.needSchool(), request.needBirthDate(),
                request.needAcademicStatus(), request.documentScaleType(), request.interviewScaleType()
        );

        if (isTemporary) recruitment.markAsTemporary();
        else recruitment.markAsFinal();

        return recruitment;
    }

    private List<RecruitmentResponseDTO.SummaryForHome> findCurrentSummaries(List<Long> orgIds) {
        if (orgIds.isEmpty()) return List.of();

        LocalDate today = LocalDate.now();
        List<Recruitment> all = recruitmentRepository.findByOrganization_IdIn(orgIds);

        return all.stream()
                .filter(r -> {
                    LocalDate created   = r.getCreatedAt().toLocalDate();
                    LocalDate endPlusOne= r.getFinalResultDate().plusDays(1);
                    return !created.isAfter(today) && !today.isAfter(endPlusOne);
                })
                .map(r -> {
                    List<RecruitmentResponseDTO.PositionCount> counts = r.getPositions().stream()
                            .map(pos -> new RecruitmentResponseDTO.PositionCount(
                                    pos.getName(),
                                    applicationRepository.countByRecruitment_IdAndPosition_Id(r.getId(), pos.getId())
                            ))
                            .toList();
                    return RecruitmentResponseDTO.SummaryForHome.from(r, counts);
                })
                .toList();
    }

    private String generateUniqueSlug() {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            String slug = SlugGenerator.generateRandomSlug();
            if (!recruitmentRepository.existsByUrlSlug(slug)) {
                return slug;
            }
        }
        throw new CustomException(ErrorCode.SLUG_GENERATION_FAILED);
    }
}
