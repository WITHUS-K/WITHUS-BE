package KUSITMS.WITHUS.domain.recruitment.recruitment.service;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper.AvailableTimeRangeAppender;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper.DocumentQuestionAppender;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper.EvaluationCriteriaAppender;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper.PositionAppender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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


    private RecruitmentResponseDTO.Create saveRecruitment(RecruitmentRequestDTO.Upsert request, boolean isTemporary) {
        Organization organization = organizationRepository.getById(request.organizationId());

        Recruitment recruitment = (request.recruitmentId() != null)
                ? updateRecruitment(request, isTemporary)
                : createRecruitment(request, organization, isTemporary);

        recruitment.clearEvaluationCriteria();
        positionAppender.append(recruitment, request.positions());
        criteriaAppender.appendWithPositions(recruitment, request.documentEvaluationCriteria(), EvaluationType.DOCUMENT);
        criteriaAppender.appendWithPositions(recruitment, request.interviewEvaluationCriteria(), EvaluationType.INTERVIEW);
        availableTimeRangeAppender.append(recruitment, request.availableTimeRanges());
        documentQuestionAppender.append(recruitment, request.applicationQuestions());

        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return RecruitmentResponseDTO.Create.from(savedRecruitment);
    }

    private Recruitment createRecruitment(RecruitmentRequestDTO.Upsert request, Organization organization, boolean isTemporary) {
        return Recruitment.create(
                request.title(), request.content(), request.fileUrl(), request.documentDeadline(),
                request.documentResultDate(), request.finalResultDate(), request.interviewDuration(), organization,
                request.needGender(), request.needAddress(), request.needSchool(), request.needBirthDate(),
                request.needAcademicStatus(), isTemporary, request.documentScaleType(), request.interviewScaleType()
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
}
