package KUSITMS.WITHUS.domain.organization.organizationRole.repository;

import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRoleJpaRepository extends JpaRepository<OrganizationRole, Long> {
    boolean existsByOrganizationIdAndName(Long organizationId, String name);
}