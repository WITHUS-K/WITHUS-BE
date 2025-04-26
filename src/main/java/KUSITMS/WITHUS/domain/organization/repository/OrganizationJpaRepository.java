package KUSITMS.WITHUS.domain.organization.repository;

import KUSITMS.WITHUS.domain.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationJpaRepository extends JpaRepository<Organization, Long> {
    List<Organization> findByNameContaining(String keyword);
    boolean existsByName(String name);
}
