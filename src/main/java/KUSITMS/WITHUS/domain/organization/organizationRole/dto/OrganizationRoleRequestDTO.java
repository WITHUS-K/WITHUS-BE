package KUSITMS.WITHUS.domain.organization.organizationRole.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "조직 내 역할 관련 요청 DTO")
public class OrganizationRoleRequestDTO {

    @Schema(description = "운영진에게 역할 부여 요청 DTO")
    public record Assign(
            @Schema(description = "운영진 ID", example = "1")
            @NotNull Long userId,

            @Schema(description = "역할 ID", example = "[1, 2]")
            @NotNull List<Long> roleIds
    ) {}

    @Schema(description = "조직의 역할 추가 요청 DTO")
    public record Create(
            @Schema(description = "역할 이름", example = "학회장")
            @NotBlank String name,

            @Schema(description = "색상", example = "blue")
            @NotBlank String color
    ) {}

    @Schema(description = "특정 역할에 여러 사용자 할당 요청 DTO")
    public record AssignUsersToRole(
            @Schema(description = "추가할 사용자 ID 리스트", example = "[1, 2, 3]")
            @NotEmpty List<Long> userIds
    ) {}

    @Schema(description = "조직의 역할 수정 요청 DTO")
    public record Update(
            @Schema(description = "수정할 역할 이름", example = "부학회장")
            @NotBlank String name,

            @Schema(description = "수정할 색상", example = "green")
            @NotBlank String color
    ) {}
}