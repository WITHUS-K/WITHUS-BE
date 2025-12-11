package KUSITMS.WITHUS.domain.user.userOrganization.controller;

import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationResponseDTO;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.userOrganization.dto.UserOrganizationRequestDTO;
import KUSITMS.WITHUS.domain.user.userOrganization.dto.UserOrganizationResponseDTO;
import KUSITMS.WITHUS.domain.user.userOrganization.service.UserOrganizationService;
import KUSITMS.WITHUS.global.common.annotation.CurrentUser;
import KUSITMS.WITHUS.global.infra.email.MailProperties;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "조직 운영진 Controller")
@RequestMapping("api/v1/organizations")
public class UserOrganizationController {

    private final UserOrganizationService organizationUserService;
    private final MailProperties mailProperties;

    @GetMapping("/{organizationId}/users")
    @Operation(summary = "조직 사용자 목록 조회", description = "조직에 소속된 운영진 목록을 조회합니다.")
    public SuccessResponse<Page<UserResponseDTO.DetailProfile>> getMembers(
            @PathVariable Long organizationId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return SuccessResponse.ok(organizationUserService.getUsers(organizationId, page - 1, size));
    }

    @PostMapping("/{organizationId}/users")
    @Operation(summary = "조직에 사용자 일괄 추가", description = "조직에 여러 명의 사용자를 한 번에 추가합니다.")
    public SuccessResponse<List<UserOrganizationResponseDTO.Detail>> addUserToOrganization(
            @PathVariable Long organizationId,
            @RequestBody @Valid UserOrganizationRequestDTO.AddUsers request
    ) {
        List<UserOrganizationResponseDTO.Detail> response = organizationUserService.addUserToOrganization(organizationId, request.userIds());
        return SuccessResponse.ok(response);
    }

    @DeleteMapping("/{organizationId}/users")
    @Operation(summary = "조직 사용자 일괄 삭제", description = "조직에서 선택한 사용자들을 일괄 삭제합니다.")
    public SuccessResponse<String> removeUsersFromOrganization(
            @PathVariable Long organizationId,
            @RequestBody @Valid UserOrganizationRequestDTO.DeleteUsers request
    ) {
        organizationUserService.removeUsers(organizationId, request.userIds());
        return SuccessResponse.ok("운영진 삭제에 성공하였습니다.");
    }

    @GetMapping("/{organizationId}/users/search")
    @Operation(summary = "조직 운영진 전체 조회 및 검색", description = "조직 ID를 기준으로 운영진 목록을 전부 조회하고 keyword, 역할ID가 존재하면 검색합니다.")
    public SuccessResponse<List<UserResponseDTO.SummaryForSearch>> getAllUsersByOrganization(
            @PathVariable Long organizationId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long roleId
    ) {
        List<UserResponseDTO.SummaryForSearch> response = organizationUserService.getUsersWithAssignment(organizationId, keyword, roleId);
        return SuccessResponse.ok(response);
    }

    @PostMapping("/{organizationId}/users/invite")
    @Operation(summary = "조직 사용자 초대 메일 전송", description = "조직에 사용자를 초대하는 메일을 발송합니다.")
    public SuccessResponse<String> sendInvitationEmails(
            @PathVariable Long organizationId,
            @RequestBody @Valid UserOrganizationRequestDTO.InviteUsers request,
            @CurrentUser User user
    ) {
        organizationUserService.sendInvitationEmails(organizationId, request.userIds(), user.getName());
        return SuccessResponse.ok("초대 메일을 전송했습니다.");
    }

    @PostMapping("/invite/accept")
    @Operation(summary = "초대 수락(이미 회원인 경우)", description = "초대받은 조직에 초대코드를 통해서 가입한다.")
    public SuccessResponse<List<OrganizationResponseDTO.Create>> acceptInvitationPost(
            @RequestBody @Valid UserOrganizationRequestDTO.InviteCode request,
            @CurrentUser User user
    ) {
        return SuccessResponse.ok(organizationUserService.acceptInvitation(request.code(), user.getId()));
    }
}
