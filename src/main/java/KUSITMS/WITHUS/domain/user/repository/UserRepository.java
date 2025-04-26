package KUSITMS.WITHUS.domain.user.repository;

import KUSITMS.WITHUS.domain.user.entity.User;

public interface UserRepository {
    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);
    User findByEmail(String email);
    User save(User user);
    User getById(Long id);
}
