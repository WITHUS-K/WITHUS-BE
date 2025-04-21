package KUSITMS.WITHUS.domain.user.repository;

import KUSITMS.WITHUS.domain.user.entity.User;

import java.util.List;

public interface UserRepository {
    Boolean existsByEmail(String email);
    User findByEmail(String email);
    User save(User user);
    User getById(Long id);
    List<User> findAllById(List<Long> userIds);
}
