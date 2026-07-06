package KUSITMS.WITHUS.domain.organization.organizationRoleGroup.service;

import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.dto.OrganizationRoleGroupRequestDTO;
import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.dto.OrganizationRoleGroupResponseDTO;

import java.util.List;

public interface OrganizationRoleGroupService {
    OrganizationRoleGroupResponseDTO.Detail create(Long userId, Long organizationId, OrganizationRoleGroupRequestDTO.Create request);
    OrganizationRoleGroupResponseDTO.Detail assignRoles(Long userId, Long organizationId, Long groupId, OrganizationRoleGroupRequestDTO.AssignRoles request);
    List<OrganizationRoleGroupResponseDTO.Detail> getGroups(Long userId, Long organizationId);
}
