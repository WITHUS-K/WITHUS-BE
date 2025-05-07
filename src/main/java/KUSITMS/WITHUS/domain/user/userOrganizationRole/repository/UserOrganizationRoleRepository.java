package KUSITMS.WITHUS.domain.user.userOrganizationRole.repository;

import KUSITMS.WITHUS.domain.user.userOrganizationRole.entity.UserOrganizationRole;

import java.util.List;

public interface UserOrganizationRoleRepository {
    UserOrganizationRole save(UserOrganizationRole userOrganizationRole);
    void deleteAll(List<UserOrganizationRole> userOrganizationRoles);
    void saveAll(List<UserOrganizationRole> userOrganizationRoles);
}