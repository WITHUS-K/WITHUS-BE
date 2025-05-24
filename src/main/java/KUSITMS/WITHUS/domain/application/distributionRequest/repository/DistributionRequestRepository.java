package KUSITMS.WITHUS.domain.application.distributionRequest.repository;

import KUSITMS.WITHUS.domain.application.distributionRequest.entity.DistributionRequest;

public interface DistributionRequestRepository {
    DistributionRequest findTopByRecruitmentIdOrderByCreatedAtDesc(Long recruitmentId);
    DistributionRequest save(DistributionRequest distributionRequest);
}
