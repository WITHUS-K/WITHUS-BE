package KUSITMS.WITHUS.domain.user.user.repository;

import KUSITMS.WITHUS.domain.user.user.entity.User;

import java.util.List;

public interface UserRepository {
    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);
    User findByEmail(String email);
    User save(User user);
    User getById(Long id);
    User getByEmail(String email);
    List<User> findAllById(List<Long> userIds);
    User getByEmailWithOrgRoles(String email);
}
