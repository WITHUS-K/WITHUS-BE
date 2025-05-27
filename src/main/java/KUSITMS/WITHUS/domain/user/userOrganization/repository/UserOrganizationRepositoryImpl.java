package KUSITMS.WITHUS.domain.user.userOrganization.repository;

import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
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
    public List<User> findListByOrganizationId(Long organizationId) {
        return queryFactory
                .select(user)
                .from(userOrganization)
                .join(userOrganization.user, user)
                .where(
                        userOrganization.organization.id.eq(organizationId),
                        user.role.eq(Role.USER)
                )
                .fetch();

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
    public List<User> findUsersByOrganizationAndKeyword(Long orgId, String keyword) {
        return queryFactory
                .select(user)
                .from(userOrganization)
                .join(userOrganization.user, user)
                .where(
                        userOrganization.organization.id.eq(orgId),
                        keyword != null ? user.name.containsIgnoreCase(keyword).or(user.email.containsIgnoreCase(keyword)) : null
                )
                .distinct()
                .fetch();
    }

    @Override
    public List<UserOrganization> findByUser_Id(Long userId) {
        return userOrganizationJpaRepository.findByUser_Id(userId);
    }

    @Override
    public boolean existsByUser_IdAndOrganization_Id(Long userId, Long organizationId) {
        return userOrganizationJpaRepository.existsByUser_IdAndOrganization_Id(userId, organizationId);
    }
}
