package KUSITMS.WITHUS.domain.recruitment.service;

import KUSITMS.WITHUS.domain.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.dto.RecruitmentResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.repository.RecruitmentRepository;
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

    /**
     * 공고 생성
     * @param request 공고 요청
     * @return 생성된 공고 응답
     */
    @Override
    @Transactional
    public RecruitmentResponseDTO.Create create(RecruitmentRequestDTO.Create request) {
        Organization organization = organizationRepository.getById(request.organizationId());

        Recruitment recruitment = Recruitment.create(
                request.title(),
                request.content(),
                request.fileUrl(),
                request.documentDeadline(),
                request.documentResultDate(),
                request.finalResultDate(),
                organization
        );

        Recruitment saved = recruitmentRepository.save(recruitment);

        return RecruitmentResponseDTO.Create.from(saved);
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
                request.title(),
                request.content(),
                request.fileUrl(),
                request.documentDeadline(),
                request.documentResultDate(),
                request.finalResultDate()
        );

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
     * 모집 공고 전체 목록 조회
     * @return 모집 공고 요약 정보 리스트
     */
    @Override
    public List<RecruitmentResponseDTO.Summary> getAll() {
        return recruitmentRepository.findAll().stream()
                .map(RecruitmentResponseDTO.Summary::from)
                .toList();
    }
}
