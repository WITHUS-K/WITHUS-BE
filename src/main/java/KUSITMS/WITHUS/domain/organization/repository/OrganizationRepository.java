package KUSITMS.WITHUS.domain.organization.repository;

import KUSITMS.WITHUS.domain.organization.entity.Organization;

import java.util.List;

public interface OrganizationRepository {
    Organization getById(Long id);
    List<Organization> findAll();
    Organization save(Organization organization);
    void delete(Long id);
    List<Organization> findByNameContaining(String keyword);
    boolean existsByName(String name);
}
