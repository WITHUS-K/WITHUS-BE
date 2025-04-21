package KUSITMS.WITHUS.domain.user.userOrganization.repository;

import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;

import java.util.List;

public interface UserOrganizationRepository {
    UserOrganization save(UserOrganization userOrganization);
    List<User> findUsersByOrganizationId(Long organizationId);
    boolean existsByUserIdAndOrganizationId(Long userId, Long organizationId);
}
