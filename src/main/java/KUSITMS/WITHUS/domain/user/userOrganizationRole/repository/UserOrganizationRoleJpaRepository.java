package KUSITMS.WITHUS.domain.user.userOrganizationRole.repository;

import KUSITMS.WITHUS.domain.user.userOrganizationRole.entity.UserOrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOrganizationRoleJpaRepository extends JpaRepository<UserOrganizationRole, Long> {
    List<UserOrganizationRole> findAllByOrganizationRole_Id(Long organizationRoleId);
}