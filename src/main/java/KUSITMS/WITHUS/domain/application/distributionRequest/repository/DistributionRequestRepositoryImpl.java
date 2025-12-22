package KUSITMS.WITHUS.domain.application.distributionRequest.repository;

import KUSITMS.WITHUS.domain.application.distributionRequest.entity.DistributionRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.application.distributionRequest.entity.QDistributionRequest.distributionRequest;
import static KUSITMS.WITHUS.domain.application.distributionRequest.entity.QDistributionAssignment.distributionAssignment;
import static KUSITMS.WITHUS.domain.organization.organizationRole.entity.QOrganizationRole.organizationRole;

@Repository
@RequiredArgsConstructor
public class DistributionRequestRepositoryImpl implements DistributionRequestRepository {

    private final DistributionRequestJpaRepository distributionRequestJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public DistributionRequest findTopByRecruitmentIdOrderByCreatedAtDesc(Long recruitmentId) {
        List<DistributionRequest> distributionRequests = findAllByRecruitmentIdOrderByCreatedAtDesc(recruitmentId);

        if (distributionRequests.isEmpty()) {
            return null;
        }

        return distributionRequests.get(0);
    }

    @Override
    public DistributionRequest save(DistributionRequest distributionRequest) {
        return distributionRequestJpaRepository.save(distributionRequest);
    }

    public List<DistributionRequest> findAllByRecruitmentIdOrderByCreatedAtDesc(Long recruitmentId) {
        return queryFactory
                .selectFrom(distributionRequest)
                .distinct()
                .leftJoin(distributionRequest.assignments, distributionAssignment).fetchJoin()
                .leftJoin(distributionAssignment.organizationRole, organizationRole).fetchJoin()
                .where(distributionRequest.recruitmentId.eq(recruitmentId))
                .orderBy(distributionRequest.createdAt.desc())
                .fetch();
    }

}
