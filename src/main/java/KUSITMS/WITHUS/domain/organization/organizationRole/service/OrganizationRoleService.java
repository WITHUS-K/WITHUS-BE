package KUSITMS.WITHUS.domain.organization.organizationRole.service;

import KUSITMS.WITHUS.domain.organization.organizationRole.dto.OrganizationRoleResponseDTO;

public interface OrganizationRoleService {
    OrganizationRoleResponseDTO.DetailForUser assignRoleToUser(Long organizationId, Long userId, Long roleId);
    OrganizationRoleResponseDTO.Detail createRole(Long organizationId, String name);
}