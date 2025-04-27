package KUSITMS.WITHUS.domain.organization.organizationRole.repository;

import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;

public interface OrganizationRoleRepository {
    OrganizationRole getById(Long id);
    OrganizationRole save(OrganizationRole organizationRole);
    boolean existsByOrganizationIdAndName(Long organizationId, String name);
}