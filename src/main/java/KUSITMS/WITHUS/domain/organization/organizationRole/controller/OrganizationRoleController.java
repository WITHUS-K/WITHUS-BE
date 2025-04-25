package KUSITMS.WITHUS.domain.organization.organizationRole.controller;

import KUSITMS.WITHUS.domain.organization.organizationRole.dto.OrganizationRoleRequestDTO;
import KUSITMS.WITHUS.domain.organization.organizationRole.dto.OrganizationRoleResponseDTO;
import KUSITMS.WITHUS.domain.organization.organizationRole.service.OrganizationRoleService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "조직 역할 관리 API")
@RequestMapping("/api/v1/organizations")
public class OrganizationRoleController {

    private final OrganizationRoleService organizationRoleService;

    @PostMapping("/{organizationId}/assign-role")
    @Operation(summary = "운영진에게 역할 부여", description = "특정 유저에게 조직 내 역할을 부여합니다.")
    public SuccessResponse<List<OrganizationRoleResponseDTO.DetailForUser>> assignRoleToUser(
            @PathVariable Long organizationId,
            @RequestBody @Valid OrganizationRoleRequestDTO.Assign request
    ) {
        List<OrganizationRoleResponseDTO.DetailForUser> response = organizationRoleService.assignRoleToUser(organizationId, request.userId(), request.roleIds());
        return SuccessResponse.ok(response);
    }

    @PostMapping("/{organizationId}/roles")
    @Operation(summary = "조직에 역할 생성", description = "조직에 새로운 역할(직책)을 추가합니다.")
    public SuccessResponse<OrganizationRoleResponseDTO.Detail> createOrganizationRole(
            @PathVariable Long organizationId,
            @RequestBody @Valid OrganizationRoleRequestDTO.Create request
    ) {
        OrganizationRoleResponseDTO.Detail response = organizationRoleService.createRole(organizationId, request.name());
        return SuccessResponse.ok(response);
    }
}
