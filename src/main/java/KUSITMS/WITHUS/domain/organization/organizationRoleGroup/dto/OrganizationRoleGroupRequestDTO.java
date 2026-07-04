package KUSITMS.WITHUS.domain.organization.organizationRoleGroup.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "조직 역할 그룹 요청 DTO")
public class OrganizationRoleGroupRequestDTO {

    @Schema(description = "조직 역할 그룹 생성 요청 DTO")
    public record Create(
            @Schema(description = "그룹 이름", example = "일반 파트")
            @NotBlank String name,

            @Schema(description = "최소 선택 개수", example = "1")
            @Min(0) int selectionMinCount,

            @Schema(description = "최대 선택 개수", example = "1")
            @Min(1) int selectionMaxCount
    ) {
    }

    @Schema(description = "조직 역할 그룹에 역할 배정 요청 DTO")
    public record AssignRoles(
            @Schema(description = "배정할 조직 역할 ID 리스트", example = "[1, 2]")
            @NotEmpty List<@NotNull Long> roleIds
    ) {
    }
}
