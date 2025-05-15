package KUSITMS.WITHUS.domain.recruitment.recruitment.service;

import KUSITMS.WITHUS.domain.application.application.repository.ApplicationJpaRepository;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentJpaRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper.AvailableTimeRangeAppender;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper.DocumentQuestionAppender;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper.EvaluationCriteriaAppender;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper.PositionAppender;
import KUSITMS.WITHUS.domain.recruitment.recruitment.util.SlugGenerator;
import KUSITMS.WITHUS.domain.user.userOrganization.repository.UserOrganizationJpaRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitmentServiceImpl implements RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final OrganizationRepository organizationRepository;

    private final AvailableTimeRangeAppender availableTimeRangeAppender;
    private final PositionAppender positionAppender;
    private final DocumentQuestionAppender documentQuestionAppender;
    private final EvaluationCriteriaAppender criteriaAppender;

    private static final int MAX_ATTEMPTS = 10;
    private final UserOrganizationJpaRepository userOrganizationJpaRepository;
    private final RecruitmentJpaRepository recruitmentJpaRepository;
    private final ApplicationJpaRepository applicationJpaRepository;

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
    @Transactional(readOnly = true)
    public <R> R getByIdAs(Long id, Function<Recruitment,R> mapper) {
        Recruitment rec = recruitmentRepository.getById(id);

        return mapper.apply(rec);
    }

    /**
     * 특정 유저가 속한 조직에서 현재 진행 중인 공고의 요약 정보를 조회합니다.
     * @param userId        요청한 유저의 ID
     * @param organizationId 조회할 조직의 ID (유저가 속해 있는 조직이어야 함)
     * @return 해당 조직의 현재 진행 중인 공고 요약 리스트
     */
    @Override
    public List<RecruitmentResponseDTO.SummaryForHome> getCurrentSummariesForUser(Long userId, Long organizationId) {

        boolean isMember = userOrganizationJpaRepository
                .existsByUser_IdAndOrganization_Id(userId, organizationId);
        if (!isMember) {
            throw new CustomException(ErrorCode.USER_ORGANIZATION_NOT_FOUND);
        }

        return findCurrentSummaries(List.of(organizationId));
    }

    /**
     * 관리자로 로그인한 유저가 매핑된 단일 조직에서 현재 진행 중인 공고의 요약 정보를 조회합니다.
     * @param adminUserId 관리자 유저의 ID
     * @return 관리자 조직의 현재 진행 중인 공고 요약 리스트
     */
    @Override
    public List<RecruitmentResponseDTO.SummaryForHome> getCurrentSummariesForAdmin(Long adminUserId) {
        // Admin 은 하나의 조직에만 매핑되어 있다고 가정
        Long orgId = userOrganizationJpaRepository.findByUser_Id(adminUserId).stream()
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_EXIST))
                .getOrganization().getId();

        return findCurrentSummaries(List.of(orgId));
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
                request.title(), request.content(), request.fileUrl(), request.documentDeadline(),
                request.documentResultDate(), request.finalResultDate(), request.interviewDuration(),
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
                request.title(), request.content(), request.fileUrl(), request.documentDeadline(),
                request.documentResultDate(), request.finalResultDate(), request.interviewDuration(), organization,
                request.needGender(), request.needAddress(), request.needSchool(), request.needBirthDate(),
                request.needAcademicStatus(), isTemporary, request.documentScaleType(), request.interviewScaleType(), generateUniqueSlug()
        );
    }

    private Recruitment updateRecruitment(RecruitmentRequestDTO.Upsert request, boolean isTemporary) {
        Recruitment recruitment = recruitmentRepository.getById(request.recruitmentId());

        recruitment.update(
                request.title(), request.content(), request.fileUrl(), request.documentDeadline(),
                request.documentResultDate(), request.finalResultDate(), request.interviewDuration(),
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
        List<Recruitment> all = recruitmentJpaRepository.findByOrganization_IdIn(orgIds);

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
                                    applicationJpaRepository.countByRecruitment_IdAndPosition_Id(r.getId(), pos.getId())
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
