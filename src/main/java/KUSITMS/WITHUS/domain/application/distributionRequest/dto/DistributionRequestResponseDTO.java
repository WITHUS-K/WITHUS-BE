package KUSITMS.WITHUS.domain.application.distributionRequest.dto;

import KUSITMS.WITHUS.domain.application.distributionRequest.entity.DistributionAssignment;
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
                    .map(Assignment::from)
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
            @Schema(description = "지원 파트 명") String positionName,
            @Schema(description = "organizationRole 명(평가 담당자)") String organizationRoleName,
            @Schema(description = "평가 타입") EvaluationType evaluationType,
            @Schema(description = "지원서당 배정한 평가자 수") int count
    ) {
        public static Assignment from(DistributionAssignment a) {
            return new Assignment(
                    a.getPosition().getName(),
                    a.getOrganizationRole().getName(),
                    a.getEvaluationType(),
                    a.getCount()
            );
        }
    }
}
