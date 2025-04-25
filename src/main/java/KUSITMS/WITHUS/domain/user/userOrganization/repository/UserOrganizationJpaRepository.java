package KUSITMS.WITHUS.domain.user.userOrganization.repository;

import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOrganizationJpaRepository extends JpaRepository<UserOrganization, Long> {
}
