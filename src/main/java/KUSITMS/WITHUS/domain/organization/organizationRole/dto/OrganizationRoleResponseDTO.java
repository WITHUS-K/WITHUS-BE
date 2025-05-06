package KUSITMS.WITHUS.domain.organization.organizationRole.dto;

import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.user.userOrganizationRole.entity.UserOrganizationRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "조직 내 역할 관련 응답 DTO")
public class OrganizationRoleResponseDTO {

    @Schema(description = "역할 추가 응답 DTO")
    public record Detail(
            @Schema(description = "ID") Long id,
            @Schema(description = "역할 이름") String roleName,
            @Schema(description = "역할 색상") String color
    ) {
        public static Detail from(OrganizationRole organizationRole) {
            return new Detail(
                    organizationRole.getId(),
                    organizationRole.getName(),
                    organizationRole.getColor()
            );
        }
    }

    @Schema(description = "역할 상세 정보 응답 DTO")
    public record RoleDetail(
            @Schema(description = "ID") Long id,
            @Schema(description = "역할 이름") String roleName,
            @Schema(description = "역할 색상") String color,
            @Schema(description = "소속된 운영진 수") int assignedUserCount
    ) {
        public static RoleDetail from(OrganizationRole role) {
            return new RoleDetail(
                    role.getId(),
                    role.getName(),
                    role.getColor(),
                    role.getUserOrganizationRoles().size()
            );
        }
    }

    @Schema(description = "조직에서 사용할 역할 리스트 반환 응답 DTO")
    public record DetailForOrganization(
            @Schema(description = "역할 갯수") int totalRoleCount,
            @Schema(description = "역할 정보 리스트") List<RoleDetail> roles
    ) {
        public static DetailForOrganization from(List<RoleDetail> roles) {
            return new DetailForOrganization(roles.size(), roles);
        }
    }

    @Schema(description = "운영진에게 역할 추가 응답 DTO")
    public record DetailForUser(
            @Schema(description = "ID") Long id,
            @Schema(description = "운영진 이름") String userName,
            @Schema(description = "역할 이름") String roleName
    ) {
        public static DetailForUser from(UserOrganizationRole userOrganizationRole) {
            return new DetailForUser(
                    userOrganizationRole.getId(),
                    userOrganizationRole.getUser().getName(),
                    userOrganizationRole.getOrganizationRole().getName()
            );
        }
    }
}