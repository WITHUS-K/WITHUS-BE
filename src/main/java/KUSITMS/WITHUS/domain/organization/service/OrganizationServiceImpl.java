package KUSITMS.WITHUS.domain.organization.service;

import KUSITMS.WITHUS.domain.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.dto.OrganizationResponseDTO;
import KUSITMS.WITHUS.domain.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    /**
     * 조직 생성
     * @param request 조직 이름을 입력받습니다.
     * @return 생성된 조직 정보(ID, 조직명) 반환
     */
    @Override
    @Transactional
    public OrganizationResponseDTO.Create create(OrganizationRequestDTO.Create request) {
        Organization organization = Organization.create(request.name());

        return OrganizationResponseDTO.Create.from(organizationRepository.save(organization));
    }

    /**
     * ID로 조직 단건 조회
     * @param id 조회할 조직의 ID 입력
     * @return 조직 상세 정보 반환
     */
    @Override
    public OrganizationResponseDTO.Detail getById(Long id) {
        Organization organization = organizationRepository.getById(id);

        return OrganizationResponseDTO.Detail.from(organization);
    }

    /**
     * 조직 정보 수정
     * @param id 수정할 조직의 ID
     * @param request 수정 정보(조직명)
     * @return 수정된 조직 정보(ID, 조직명) 반환
     */
    @Override
    @Transactional
    public OrganizationResponseDTO.Update update(Long id, OrganizationRequestDTO.Update request) {
        Organization organization = organizationRepository.getById(id);

        organization.updateName(request.name());

        return OrganizationResponseDTO.Update.from(organization);
    }

    /**
     * ID로 조직 삭제
     * @param id 삭제할 조직의 ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        organizationRepository.getById(id);
        organizationRepository.delete(id);
    }

    /**
     * 조직 전체 조회
     * @return 저장된 조직 전체를 반환
     */
    @Override
    public List<OrganizationResponseDTO.Summary> getAll() {
        return organizationRepository.findAll().stream()
                .map(OrganizationResponseDTO.Summary::from)
                .toList();
    }
}
