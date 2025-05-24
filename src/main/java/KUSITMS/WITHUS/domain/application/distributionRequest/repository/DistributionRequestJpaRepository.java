package KUSITMS.WITHUS.domain.application.distributionRequest.repository;

import KUSITMS.WITHUS.domain.application.distributionRequest.entity.DistributionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistributionRequestJpaRepository extends JpaRepository<DistributionRequest, Long> {
    List<DistributionRequest> findAllByRecruitmentIdOrderByCreatedAtDesc(Long recruitmentId);
}
