package KUSITMS.WITHUS.domain.organization.organizationRoleGroup.controller;

import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.dto.OrganizationRoleGroupRequestDTO;
import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.dto.OrganizationRoleGroupResponseDTO;
import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.service.OrganizationRoleGroupService;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.common.annotation.CurrentUser;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "조직 역할 그룹 관리 API")
@RequestMapping("/api/v1/organizations")
public class OrganizationRoleGroupController {

    private final OrganizationRoleGroupService organizationRoleGroupService;

    @PostMapping("/{organizationId}/role-groups")
    @Operation(summary = "조직 역할 그룹 생성", description = "조직 단위로 재사용할 역할 그룹을 생성합니다.")
    public SuccessResponse<OrganizationRoleGroupResponseDTO.Detail> create(
            @CurrentUser User currentUser,
            @PathVariable Long organizationId,
            @RequestBody @Valid OrganizationRoleGroupRequestDTO.Create request
    ) {
        return SuccessResponse.ok(organizationRoleGroupService.create(currentUser.getId(), organizationId, request));
    }

    @PutMapping("/{organizationId}/role-groups/{groupId}/roles")
    @Operation(summary = "조직 역할 그룹에 역할 배정", description = "기존 조직 역할들을 특정 역할 그룹에 배정합니다.")
    public SuccessResponse<OrganizationRoleGroupResponseDTO.Detail> assignRoles(
            @CurrentUser User currentUser,
            @PathVariable Long organizationId,
            @PathVariable Long groupId,
            @RequestBody @Valid OrganizationRoleGroupRequestDTO.AssignRoles request
    ) {
        return SuccessResponse.ok(organizationRoleGroupService.assignRoles(currentUser.getId(), organizationId, groupId, request));
    }

    @GetMapping("/{organizationId}/role-groups")
    @Operation(summary = "조직 역할 그룹 목록 조회", description = "조직에 등록된 역할 그룹과 소속 역할을 조회합니다.")
    public SuccessResponse<List<OrganizationRoleGroupResponseDTO.Detail>> getGroups(
            @CurrentUser User currentUser,
            @PathVariable Long organizationId
    ) {
        return SuccessResponse.ok(organizationRoleGroupService.getGroups(currentUser.getId(), organizationId));
    }
}
