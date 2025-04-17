package KUSITMS.WITHUS.domain.application.service;

import KUSITMS.WITHUS.domain.application.dto.PositionRequestDTO;
import KUSITMS.WITHUS.domain.application.dto.PositionResponseDTO;
import KUSITMS.WITHUS.domain.application.entity.Position;
import KUSITMS.WITHUS.domain.application.repository.PositionRepository;
import KUSITMS.WITHUS.domain.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final OrganizationRepository organizationRepository;

    /**
     * 파트 생성
     * @param request 생성 요청 DTO
     * @return 생성된 파트 상세 정보
     */
    @Override
    @Transactional
    public PositionResponseDTO.Detail create(PositionRequestDTO.Create request) {
        Organization organization = organizationRepository.getById(request.organizationId());

        Position position = Position.builder()
                .name(request.name())
                .organization(organization)
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

    /**
     * 조직 ID로 파트 목록 조회
     * @param organizationId 조직 ID
     * @return 해당 조직에 소속된 파트 목록
     */
    @Override
    public List<PositionResponseDTO.Detail> getByOrganization(Long organizationId) {
        return positionRepository.findByOrganizationId(organizationId).stream()
                .map(PositionResponseDTO.Detail::from)
                .toList();
    }
}
