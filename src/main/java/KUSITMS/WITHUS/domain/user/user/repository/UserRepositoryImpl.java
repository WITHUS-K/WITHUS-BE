package KUSITMS.WITHUS.domain.user.user.repository;

import KUSITMS.WITHUS.domain.organization.organization.entity.QOrganization;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.QOrganizationRole;
import KUSITMS.WITHUS.domain.user.user.entity.QUser;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.userOrganizationRole.entity.QUserOrganizationRole;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final JPAQueryFactory queryFactory;

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

    @Override
    public User getByEmailWithOrgRoles(String email) {
        QUser user = QUser.user;
        QUserOrganizationRole userOrganizationRole = QUserOrganizationRole.userOrganizationRole;
        QOrganizationRole organizationRole = QOrganizationRole.organizationRole;
        QOrganization organization = QOrganization.organization;

        User result = queryFactory
                .selectFrom(user)
                .leftJoin(user.userOrganizationRoles, userOrganizationRole).fetchJoin()
                .leftJoin(userOrganizationRole.organizationRole, organizationRole).fetchJoin()
                .leftJoin(organizationRole.organization, organization).fetchJoin()
                .where(user.email.eq(email))
                .fetchOne();
        if (result == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXIST);
        }
        return result;
    }
}
