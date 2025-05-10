package KUSITMS.WITHUS.domain.user.user.repository;

import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public Boolean existsByPhoneNumber(String phoneNumber) { return userJpaRepository.existsByPhoneNumber(phoneNumber); }

    @Override
    public User findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return userJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));
    }

    @Override
    public User getByEmail(String email) {
        User user = userJpaRepository.findByEmail(email);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXIST);
        }
        return user;
    }

    @Override
    public List<User> findAllById(List<Long> userIds) {
        return userJpaRepository.findAllById(userIds);
    }
}
