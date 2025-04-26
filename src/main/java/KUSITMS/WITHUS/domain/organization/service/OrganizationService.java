package KUSITMS.WITHUS.domain.organization.service;

import KUSITMS.WITHUS.domain.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.dto.OrganizationResponseDTO;
import KUSITMS.WITHUS.domain.organization.entity.Organization;

import java.util.List;

public interface OrganizationService {
    OrganizationResponseDTO.Create create(OrganizationRequestDTO.Create request);
    OrganizationResponseDTO.Detail getById(Long id);
    OrganizationResponseDTO.Update update(Long id, OrganizationRequestDTO.Update request);
    void delete(Long id);
    List<OrganizationResponseDTO.Summary> getAll();
    List<Organization> search(String keyword);
}
