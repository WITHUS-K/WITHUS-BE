package KUSITMS.WITHUS.domain.application.distributionRequest.dto;

import KUSITMS.WITHUS.domain.application.distributionRequest.entity.DistributionRequest;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "평가 담당자 분배 요청 이력 DTO")
public class DistributionRequestResponseDTO {

    @Schema(description = "평가 담당자 분배 최신 이력 조회 DTO")
    public record Detail(
            @Schema(description = "평가 담당자 분배 요청 이력 id") Long id,
            @Schema(description = "공고 id") Long recruitmentId,
            @Schema(description = "최신 요청 이력 리스트") List<Assignment> assignments
    ) {
        public static Detail from(DistributionRequest distributionRequest) {
            List<Assignment> assignments = distributionRequest.getAssignments().stream()
                    .map(a -> new Assignment(
                            a.getPositionId(),
                            a.getOrganizationRoleId(),
                            a.getEvaluationType(),
                            a.getCount()))
                    .toList();

            return new Detail(
                    distributionRequest.getId(),
                    distributionRequest.getRecruitmentId(),
                    assignments
            );
        }
    }

    @Schema(description = "평가 담당자 분배 최신 이력 리스트 DTO")
    public record Assignment(
            Long positionId,
            Long organizationRoleId,
            EvaluationType evaluationType,
            int count
    ) {}
}
