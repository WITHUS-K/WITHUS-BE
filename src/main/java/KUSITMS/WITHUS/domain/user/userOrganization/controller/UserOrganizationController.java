package KUSITMS.WITHUS.domain.user.userOrganization.controller;

import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.userOrganization.dto.UserOrganizationRequestDTO;
import KUSITMS.WITHUS.domain.user.userOrganization.dto.UserOrganizationResponseDTO;
import KUSITMS.WITHUS.domain.user.userOrganization.service.UserOrganizationService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public SuccessResponse<Page<UserResponseDTO.DetailForOrganization>> getMembers(
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
    @Operation(summary = "조직 운영진 전체 조회 및 검색", description = "조직 ID를 기준으로 운영진 목록을 전부 조회하고 keyword가 존재하면 검색합니다.")
    public SuccessResponse<List<UserResponseDTO.SummaryForSearch>> getAllUsersByOrganization(
            @PathVariable Long organizationId,
            @RequestParam(required = false) String keyword
    ) {
        List<UserResponseDTO.SummaryForSearch> response = organizationUserService.getAllUsersByOrganization(organizationId, keyword);
        return SuccessResponse.ok(response);
    }
}
