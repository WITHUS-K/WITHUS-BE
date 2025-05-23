package KUSITMS.WITHUS.domain.application.applicationEvaluator.dto;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "지원서 평가 담당자 배정 요청 DTO")
public class ApplicationEvaluatorRequestDTO {

    @Schema(description = "지원서 평가 담당자 배정 요청 DTO")
    public record Distribute(
            @Schema(description = "대상 공고 ID", example = "4")
            @NotNull
            Long recruitmentId,

            @Schema(description = "파트별 평가 담당자 배정 정보 리스트")
            @NotEmpty
            List<PartAssignment> assignments
    ) {
        @Schema(description = "지원 파트-OrganizationRole별 평가 담당자 배정 정보")
        public record PartAssignment(
                @Schema(description = "지원 파트(Position) ID", example = "4")
                @NotNull
                Long positionId,

                @Schema(description = "평가 담당자 Role(OrganizationRole) ID", example = "7")
                @NotNull
                Long organizationRoleId,

                @Schema(description = "평가 타입", example = "DOCUMENT | INTERVIEW")
                @NotNull EvaluationType evaluationType,

                @Schema(description = "지원서당 배정할 평가자 수", example = "3")
                @Min(value = 1)
                int count
        ) {}
    }

    @Schema(description = "지원서별 평가 담당자 업데이트 요청 DTO")
    public record Update(
            @Schema(description = "대상 지원서 ID", example = "1")
            @NotNull
            Long applicationId,

            @Schema(description = "평가 타입", example = "DOCUMENT | INTERVIEW")
            @NotNull EvaluationType evaluationType,

            @Schema(description = "새로 배정할 평가자 User ID 리스트", example = "[1,2,3]")
            @NotEmpty
            List<@NotNull Long> evaluatorIds
    ) {}

}
