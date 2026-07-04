package KUSITMS.WITHUS.domain.organization.organizationRoleGroup.repository;

import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.entity.OrganizationRoleGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationRoleGroupJpaRepository extends JpaRepository<OrganizationRoleGroup, Long> {
    List<OrganizationRoleGroup> findByOrganizationIdOrderByIdAsc(Long organizationId);
}
