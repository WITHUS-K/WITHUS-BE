package KUSITMS.WITHUS.domain.user.userOrganization.controller;

import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.userOrganization.dto.UserOrganizationRequestDTO;
import KUSITMS.WITHUS.domain.user.userOrganization.dto.UserOrganizationResponseDTO;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import KUSITMS.WITHUS.domain.user.userOrganization.service.UserOrganizationService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "조직 운영진 Controller")
@RequestMapping("api/v1/organizations")
public class UserOrganizationController {

    private final UserOrganizationService organizationUserService;

    @GetMapping("/{organizationId}/users")
    @Operation(summary = "조직 사용자 목록 조회", description = "조직에 소속된 운영진 목록을 조회합니다.")
    public SuccessResponse<List<UserResponseDTO.Summary>> getMembers(@PathVariable Long organizationId) {
        return SuccessResponse.ok(organizationUserService.getAllUsers(organizationId));
    }

    @PostMapping("/{organizationId}/users")
    @Operation(summary = "조직에 사용자 추가", description = "조직에 사용자를 추가합니다.")
    public SuccessResponse<UserOrganizationResponseDTO.Detail> addUserToOrganization(
            @PathVariable Long organizationId,
            @RequestBody @Valid UserOrganizationRequestDTO.AddUser request
    ) {
        UserOrganization userOrganization = organizationUserService.addUserToOrganization(organizationId, request.userId());
        return SuccessResponse.ok(UserOrganizationResponseDTO.Detail.from(userOrganization));
    }

}
