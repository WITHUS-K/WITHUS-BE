package KUSITMS.WITHUS.domain.organization.organizationRole.repository;

import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.organization.organizationRole.entity.QOrganizationRole.organizationRole;
import static KUSITMS.WITHUS.domain.user.userOrganizationRole.entity.QUserOrganizationRole.userOrganizationRole;

@Repository
@RequiredArgsConstructor
public class OrganizationRoleRepositoryImpl implements OrganizationRoleRepository {

    private final OrganizationRoleJpaRepository organizationRoleJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public OrganizationRole getById(Long id) {
        return organizationRoleJpaRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.ORGANIZATION_ROLE_NOT_EXIST));
    }

    @Override
    public OrganizationRole save(OrganizationRole organizationRole) {
        return organizationRoleJpaRepository.save(organizationRole);
    }

    @Override
    public boolean existsByOrganizationIdAndName(Long organizationId, String name) {
        return organizationRoleJpaRepository.existsByOrganizationIdAndName(organizationId, name);
    }

    @Override
    public List<OrganizationRole> findAllById(List<Long> roleIds) {
        return organizationRoleJpaRepository.findAllById(roleIds);
    }

    @Override
    public List<OrganizationRole> findByOrganizationIdAndKeyword(Long organizationId, String keyword) {
        return queryFactory
                .selectDistinct(organizationRole)
                .from(organizationRole)
                .leftJoin(organizationRole.userOrganizationRoles, userOrganizationRole).fetchJoin()
                .where(
                        organizationRole.organization.id.eq(organizationId),
                        keywordCondition(keyword)
                )
                .orderBy(organizationRole.name.asc())
                .fetch();
    }

    @Override
    public boolean existsByOrganizationIdAndNameExceptId(Long organizationId, String name, Long excludedId) {
        return queryFactory
                .selectOne()
                .from(organizationRole)
                .where(
                        organizationRole.organization.id.eq(organizationId),
                        organizationRole.name.eq(name),
                        organizationRole.id.ne(excludedId)
                )
                .fetchFirst() != null;
    }

    private BooleanExpression keywordCondition(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return organizationRole.name.startsWith(keyword);
    }
}