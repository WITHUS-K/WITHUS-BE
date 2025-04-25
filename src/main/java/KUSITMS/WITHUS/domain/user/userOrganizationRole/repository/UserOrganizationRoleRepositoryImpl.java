package KUSITMS.WITHUS.domain.user.userOrganizationRole.repository;

import KUSITMS.WITHUS.domain.user.userOrganizationRole.entity.UserOrganizationRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserOrganizationRoleRepositoryImpl implements UserOrganizationRoleRepository {

    private final UserOrganizationRoleJpaRepository userOrganizationRoleJpaRepository;

    @Override
    public UserOrganizationRole save(UserOrganizationRole userOrganizationRole) {
        return userOrganizationRoleJpaRepository.save(userOrganizationRole);
    }
}
