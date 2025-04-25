package KUSITMS.WITHUS.domain.organization.organizationRole.repository;

import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;

import java.util.List;

public interface OrganizationRoleRepository {
    OrganizationRole getById(Long id);
    OrganizationRole save(OrganizationRole organizationRole);
    boolean existsByOrganizationIdAndName(Long organizationId, String name);
    List<OrganizationRole> findAllById(List<Long> roleIds);
    List<OrganizationRole> findAllByOrganizationIdWithUsers(Long organizationId);
}
