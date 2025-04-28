package KUSITMS.WITHUS.domain.organization.organization.repository;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
