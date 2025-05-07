package KUSITMS.WITHUS.domain.organization.organizationRole.service;

import KUSITMS.WITHUS.domain.organization.organizationRole.dto.OrganizationRoleResponseDTO;

import java.util.List;

public interface OrganizationRoleService {
    List<OrganizationRoleResponseDTO.DetailForUser> assignRoleToUser(Long organizationId, Long userId, List<Long> roleIds);
    List<OrganizationRoleResponseDTO.DetailForUser> updateUsersOfRole(Long organizationId, Long roleId, List<Long> newUserIds);
    OrganizationRoleResponseDTO.Detail createRole(Long organizationId, String name, String color);
    OrganizationRoleResponseDTO.DetailForOrganization getOrganizationRoles(Long organizationId, String keyword);
    void updateRole(Long organizationId, Long roleId, String name, String color);
}