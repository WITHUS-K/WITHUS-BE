package KUSITMS.WITHUS.domain.user.userOrganization.repository;

import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserOrganizationRepository {
    UserOrganization save(UserOrganization userOrganization);
    boolean existsByUserIdAndOrganizationId(Long userId, Long organizationId);
    Page<User> findByOrganizationId(Long organizationId, Pageable pageable);
}
