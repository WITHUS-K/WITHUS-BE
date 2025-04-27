package KUSITMS.WITHUS.domain.organization.organizationRole.repository;

import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrganizationRoleRepositoryImpl implements OrganizationRoleRepository {

    private final OrganizationRoleJpaRepository organizationRoleJpaRepository;

    @Override
    public OrganizationRole getById(Long id) {
        return organizationRoleJpaRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.ORGANIZATION_ROLE_NOT_EXIST));
    }

    @Override
    public OrganizationRole save(OrganizationRole organizationRole) {
        return organizationRoleJpaRepository.save(organizationRole);
    }

    @Override
    public boolean existsByOrganizationIdAndName(Long organizationId, String name) {
        return organizationRoleJpaRepository.existsByOrganizationIdAndName(organizationId, name);
    }

    @Override
    public List<OrganizationRole> findAllById(List<Long> roleIds) {
        return organizationRoleJpaRepository.findAllById(roleIds);
    }
}