package KUSITMS.WITHUS.domain.user.userOrganization.repository;

import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Page<User> findByOrganizationId(Long organizationId, Pageable pageable) {
        List<User> content = queryFactory
                .select(user)
                .from(userOrganization)
                .join(userOrganization.user, user)
                .where(
                        userOrganization.organization.id.eq(organizationId),
                        user.role.eq(Role.USER)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = Optional.ofNullable(
                queryFactory
                        .select(user.count())
                        .from(userOrganization)
                        .join(userOrganization.user, user)
                        .where(
                                userOrganization.organization.id.eq(organizationId),
                                user.role.eq(Role.USER)
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public List<UserOrganization> findAllByOrganizationIdAndUserIdIn(Long organizationId, List<Long> userIds) {
        return queryFactory
                .selectFrom(userOrganization)
                .where(
                        userOrganization.organization.id.eq(organizationId),
                        userOrganization.user.id.in(userIds)
                )
                .fetch();
    }

    @Override
    public void deleteAllInBatch(List<UserOrganization> userOrganizations) {
        userOrganizationJpaRepository.deleteAllInBatch(userOrganizations);
    }

    @Override
    public List<User> findManagersByOrganizationId(Long organizationId, String keyword) {
        return queryFactory
                .select(user)
                .from(userOrganization)
                .join(userOrganization.user, user)
                .where(
                        userOrganization.organization.id.eq(organizationId),
                        user.role.eq(Role.USER),
                        keywordCondition(keyword)
                )
                .fetch();
    }

    private BooleanExpression keywordCondition(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return user.name.containsIgnoreCase(keyword);
    }
}
