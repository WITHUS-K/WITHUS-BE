package KUSITMS.WITHUS.domain.organization.controller;

import KUSITMS.WITHUS.domain.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.dto.OrganizationResponseDTO;
import KUSITMS.WITHUS.domain.organization.service.OrganizationService;
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
}
