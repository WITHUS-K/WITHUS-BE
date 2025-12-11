package KUSITMS.WITHUS.domain.organization.organization.repository;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrganizationRepositoryImpl implements OrganizationRepository {

    private final OrganizationJpaRepository organizationJpaRepository;

    @Override
    public Organization getById(Long id) {
        return organizationJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORGANIZATION_NOT_EXIST));
    }

    @Override
    public List<Organization> findAll() {
        return organizationJpaRepository.findAll();
    }

    @Override
    public Organization save(Organization organization) {
        return organizationJpaRepository.save(organization);
    }

    @Override
    public void delete(Long id) {
        organizationJpaRepository.deleteById(id);
    }

    @Override
    public List<Organization> findByNameContaining(String keyword) {
        return organizationJpaRepository.findByNameContaining(keyword);
    }

    @Override
    public boolean existsByName(String name) {
        return organizationJpaRepository.existsByName(name);
    }

    @Override
    public Optional<Organization> findByInviteCode(String inviteCode) { return organizationJpaRepository.findByInviteCode(inviteCode); }

    @Override
    public List<Organization> findOrganizations(List<Long> organizationIds) { return organizationJpaRepository.findAllByIdIn(organizationIds);}
}
