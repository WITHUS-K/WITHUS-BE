package KUSITMS.WITHUS.domain.organization.organization.repository;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationJpaRepository extends JpaRepository<Organization, Long> {
}
