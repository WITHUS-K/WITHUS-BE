package KUSITMS.WITHUS.domain.user.repository;

import KUSITMS.WITHUS.domain.user.entity.User;

public interface UserRepository {
    Boolean existsByEmail(String email);
    User findByEmail(String email);
    User save(User user);
    User getById(Long id);
}
