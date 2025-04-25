package KUSITMS.WITHUS.domain.user.userOrganization.repository;

import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.user.user.entity.QUser.user;
import static KUSITMS.WITHUS.domain.user.userOrganization.entity.QUserOrganization.userOrganization;

@Repository
@RequiredArgsConstructor
public class UserOrganizationRepositoryImpl implements UserOrganizationRepository {

    private final UserOrganizationJpaRepository userOrganizationJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public UserOrganization save(UserOrganization userOrganization) {
        return userOrganizationJpaRepository.save(userOrganization);
    }

    @Override
    public List<User> findUsersByOrganizationId(Long organizationId) {
        return queryFactory
                .select(user)
                .from(userOrganization)
                .join(userOrganization.user, user)
                .where(userOrganization.organization.id.eq(organizationId))
                .fetch();
    }

    @Override
    public boolean existsByUserIdAndOrganizationId(Long userId, Long organizationId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(userOrganization)
                .where(
                        userOrganization.user.id.eq(userId),
                        userOrganization.organization.id.eq(organizationId)
                )
                .fetchFirst();

        return fetchOne != null;
    }
}
