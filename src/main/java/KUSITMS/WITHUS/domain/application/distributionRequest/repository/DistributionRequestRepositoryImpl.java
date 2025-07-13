package KUSITMS.WITHUS.domain.application.distributionRequest.repository;

import KUSITMS.WITHUS.domain.application.distributionRequest.entity.DistributionRequest;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DistributionRequestRepositoryImpl implements DistributionRequestRepository {

    private final DistributionRequestJpaRepository distributionRequestJpaRepository;

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
        return distributionRequestJpaRepository.findAllByRecruitmentIdOrderByCreatedAtDesc(recruitmentId);
    }

}
