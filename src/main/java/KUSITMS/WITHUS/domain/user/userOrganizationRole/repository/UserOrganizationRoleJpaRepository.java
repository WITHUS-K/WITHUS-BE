package KUSITMS.WITHUS.domain.user.userOrganizationRole.repository;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.user.userOrganizationRole.entity.UserOrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserOrganizationRoleJpaRepository extends JpaRepository<UserOrganizationRole, Long> {
    List<UserOrganizationRole> findAllByOrganizationRole_Id(Long organizationRoleId);
    void deleteByOrganizationRole_Id(Long roleId);

    @Query("""
            select distinct organizationRole.organization
            from UserOrganizationRole userOrganizationRole
            join userOrganizationRole.organizationRole organizationRole
            where userOrganizationRole.user.id = :userId
            """)
    List<Organization> findDistinctOrganizationsByUserId(Long userId);
}
