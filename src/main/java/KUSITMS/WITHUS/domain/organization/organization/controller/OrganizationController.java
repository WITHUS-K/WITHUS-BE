package KUSITMS.WITHUS.domain.organization.organization.controller;

import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationResponseDTO;
import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.service.UserService;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "조직(동아리) Controller")
@RequestMapping("api/v1/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "조직 생성")
    public SuccessResponse<OrganizationResponseDTO.Create> create(
            @Valid @RequestBody OrganizationRequestDTO.Create request
    ) {
        return SuccessResponse.ok(organizationService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "조직 단건 조회")
    public SuccessResponse<OrganizationResponseDTO.Detail> getById(@PathVariable Long id) {
        return SuccessResponse.ok(organizationService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "조직 수정")
    public SuccessResponse<OrganizationResponseDTO.Update> update(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationRequestDTO.Update request
    ) {
        return SuccessResponse.ok(organizationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "조직 삭제")
    public SuccessResponse<String> delete(@PathVariable Long id) {
        organizationService.delete(id);
        return SuccessResponse.ok("조직 삭제에 성공하였습니다.");
    }

    @GetMapping
    @Operation(summary = "조직 전체 조회")
    public SuccessResponse<List<OrganizationResponseDTO.Summary>> getAll() {
        return SuccessResponse.ok(organizationService.getAll());
    }

    @GetMapping("/search")
    @Operation(summary = "동아리 검색 API")
    public SuccessResponse<List<OrganizationResponseDTO.Search>> searchOrganization(@RequestParam("keyword") String keyword) {
        List<Organization> organizations = organizationService.search(keyword);

        List<OrganizationResponseDTO.Search> result = organizations.stream()
                .map(OrganizationResponseDTO.Search::from)
                .toList();

        return SuccessResponse.ok(result);
    }

    @GetMapping("/organization/{organizationId}/user/{userId}")
    @Operation(summary = "조직 내 특정 사용자 상세 조회 API", description = "조직 ID와 사용자 ID를 기반으로 조직 내 사용자의 상세 정보를 조회합니다.")
    public SuccessResponse<UserResponseDTO.DetailProfile> getUserDetailInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long userId
    ) {
        User user = userService.getById(userId);
        organizationService.getById(organizationId);

        boolean isInOrganization = user.getUserOrganizationRoles().stream()
                .anyMatch(userOrganizationRole ->
                        userOrganizationRole.getOrganizationRole().getOrganization().getId().equals(organizationId)
                );

        // 조직에 속하지 않은 유저일 경우 예외 처리
        if (!isInOrganization) {
            throw new CustomException(ErrorCode.USER_NOT_IN_ORGANIZATION);
        }

        UserResponseDTO.DetailProfile response = UserResponseDTO.DetailProfile.from(user, organizationId);
        return SuccessResponse.ok(response);
    }
}
