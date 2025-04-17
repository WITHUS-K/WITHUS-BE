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

    @Override
    @Transactional
    public OrganizationResponseDTO.Create create(OrganizationRequestDTO.Create request) {
        Organization organization = Organization.create(request.name());

        return OrganizationResponseDTO.Create.from(organizationRepository.save(organization));
    }

    @Override
    public OrganizationResponseDTO.Detail getById(Long id) {
        Organization organization = organizationRepository.getById(id);

        return new OrganizationResponseDTO.Detail(
                organization.getId(),
                organization.getName()
        );
    }

    @Override
    @Transactional
    public OrganizationResponseDTO.Update update(Long id, OrganizationRequestDTO.Update request) {
        Organization organization = organizationRepository.getById(id);

        organization.updateName(request.name());

        return new OrganizationResponseDTO.Update(
                organization.getId(),
                organization.getName()
        );
    }

    @Override
    @Transactional
    public void delete(Long id) {
        organizationRepository.getById(id);
        organizationRepository.delete(id);
    }

    @Override
    public List<OrganizationResponseDTO.Summary> getAll() {
        return organizationRepository.findAll().stream()
                .map(organization -> new OrganizationResponseDTO.Summary(organization.getId(), organization.getName()))
                .toList();
    }
}
