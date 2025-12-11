package KUSITMS.WITHUS.domain.organization.organization.repository;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationJpaRepository extends JpaRepository<Organization, Long> {
    List<Organization> findByNameContaining(String keyword);
    boolean existsByName(String name);

    Optional<Organization> findByInviteCode(String inviteCode);

    List<Organization> findAllByIdIn(List<Long> organizationIds);
}
