package KUSITMS.WITHUS.domain.organization.repository;

import KUSITMS.WITHUS.domain.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationJpaRepository extends JpaRepository<Organization, Long> {
}
