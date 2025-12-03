package KUSITMS.WITHUS.domain.organization.organization.repository;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository {
    Organization getById(Long id);
    List<Organization> findAll();
    Organization save(Organization organization);
    void delete(Long id);
    List<Organization> findByNameContaining(String keyword);
    boolean existsByName(String name);
    Optional<Organization> findByInviteCode(String inviteCode);
    List<Organization> findOrganzations(List<Long> organizationIds);
}
