package KUSITMS.WITHUS.domain.organization.organizationRole.dto;

import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.user.userOrganizationRole.entity.UserOrganizationRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "조직 내 역할 관련 응답 DTO")
public class OrganizationRoleResponseDTO {

    @Schema(description = "역할 추가 응답 DTO")
    public record Detail(
            @Schema(description = "ID") Long id,
            @Schema(description = "역할 이름") String roleName
    ) {
        public static Detail from(OrganizationRole organizationRole) {
            return new Detail(
                    organizationRole.getId(),
                    organizationRole.getName()
            );
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