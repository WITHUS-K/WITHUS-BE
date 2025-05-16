package KUSITMS.WITHUS.domain.user.userOrganization.repository;

import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOrganizationJpaRepository extends JpaRepository<UserOrganization, Long> {
    List<UserOrganization> findByUser_Id(Long userId);
    boolean existsByUser_IdAndOrganization_Id(Long userId, Long organizationId);
}
