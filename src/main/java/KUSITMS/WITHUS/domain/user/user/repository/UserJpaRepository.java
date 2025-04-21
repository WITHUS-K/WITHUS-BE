package KUSITMS.WITHUS.domain.user.user.repository;

import KUSITMS.WITHUS.domain.user.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    User findByEmail(String email);
}