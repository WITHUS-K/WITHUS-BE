package KUSITMS.WITHUS.domain.user.repository;

import KUSITMS.WITHUS.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Integer> {
    Boolean existsByEmail(String email);
    User findByEmail(String email);
}