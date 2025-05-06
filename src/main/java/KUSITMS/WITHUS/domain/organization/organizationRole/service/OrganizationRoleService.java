package KUSITMS.WITHUS.domain.organization.organizationRole.service;

import KUSITMS.WITHUS.domain.organization.organizationRole.dto.OrganizationRoleResponseDTO;

import java.util.List;

public interface OrganizationRoleService {
    List<OrganizationRoleResponseDTO.DetailForUser> assignRoleToUser(Long organizationId, Long userId, List<Long> roleIds);
    OrganizationRoleResponseDTO.Detail createRole(Long organizationId, String name, String color);
    OrganizationRoleResponseDTO.DetailForOrganization getOrganizationRoles(Long organizationId, String keyword);
    List<OrganizationRoleResponseDTO.DetailForUser> assignUsersToRole(Long organizationId, Long roleId, List<Long> userIds);
}