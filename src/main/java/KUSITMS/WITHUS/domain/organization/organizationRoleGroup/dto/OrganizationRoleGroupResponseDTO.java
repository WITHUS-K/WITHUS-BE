package KUSITMS.WITHUS.domain.organization.organizationRoleGroup.dto;

import KUSITMS.WITHUS.domain.organization.organizationRole.dto.OrganizationRoleResponseDTO;
import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.entity.OrganizationRoleGroup;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "조직 역할 그룹 응답 DTO")
public class OrganizationRoleGroupResponseDTO {

    @Schema(description = "조직 역할 그룹 상세 응답 DTO")
    public record Detail(
            Long id,
            String name,
            int selectionMinCount,
            int selectionMaxCount,
            List<OrganizationRoleResponseDTO.Detail> roles
    ) {
        public static Detail from(OrganizationRoleGroup group) {
            return new Detail(
                    group.getId(),
                    group.getName(),
                    group.getSelectionMinCount(),
                    group.getSelectionMaxCount(),
                    group.getOrganizationRoles().stream()
                            .map(OrganizationRoleResponseDTO.Detail::from)
                            .toList()
            );
        }
    }
}
