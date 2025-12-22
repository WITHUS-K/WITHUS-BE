package KUSITMS.WITHUS.domain.recruitment.position.service;

import KUSITMS.WITHUS.domain.recruitment.position.dto.PositionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.position.dto.PositionResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.position.enumerate.PositionColor;
import KUSITMS.WITHUS.domain.recruitment.position.repository.PositionRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static KUSITMS.WITHUS.domain.recruitment.recruitment.entity.QRecruitment.recruitment;
import static KUSITMS.WITHUS.domain.recruitment.recruitmentOrganizationRole.entity.QRecruitmentOrganizationRole.recruitmentOrganizationRole;
import static KUSITMS.WITHUS.domain.organization.organizationRole.entity.QOrganizationRole.organizationRole;

import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final JPAQueryFactory queryFactory;

    /**
     * 파트 생성
     * @param request 생성 요청 DTO
     * @return 생성된 파트 상세 정보
     */
    @Override
    @Transactional
    public PositionResponseDTO.Detail create(PositionRequestDTO.Create request) {
        Recruitment recruitment = recruitmentRepository.getById(request.recruitmentId());

        Position position = Position.builder()
                .name(request.name())
                .recruitment(recruitment)
                .color(PositionColor.getRandomColor())
                .build();

        return PositionResponseDTO.Detail.from(positionRepository.save(position));
    }

    /**
     * 파트 삭제
     * @param id 삭제할 파트 ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        positionRepository.getById(id);
        positionRepository.delete(id);
    }

    @Override
    public List<PositionResponseDTO.Detail> findAllByRecruitmentId(Long recruitmentId) {
        // Recruitment의 RecruitmentOrganizationRole 리스트와 각각의 OrganizationRole을 함께 조회하기 위해 fetch join 사용
        Recruitment found = queryFactory
                .selectFrom(recruitment)
                .leftJoin(recruitment.positions, recruitmentOrganizationRole).fetchJoin()
                .leftJoin(recruitmentOrganizationRole.organizationRole, organizationRole).fetchJoin()
                .where(recruitment.id.eq(recruitmentId))
                .fetchOne();

        if (found == null) {
            throw new CustomException(ErrorCode.RECRUITMENT_NOT_EXIST);
        }

        return found.getPositions().stream()
                .map(ror -> PositionResponseDTO.Detail.from(ror.getOrganizationRole()))
                .toList();
    }
}
