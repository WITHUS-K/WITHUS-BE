package KUSITMS.WITHUS.domain.application.application.controller;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.enumerate.AdminStageFilter;
import KUSITMS.WITHUS.domain.application.application.service.ApplicationService;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.dto.ApplicationEvaluatorRequestDTO;
import KUSITMS.WITHUS.global.response.PagedResponse;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/applications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/recruitment/{recruitmentId}")
    public SuccessResponse<PagedResponse<ApplicationResponseDTO.SummaryForAdmin>> getList(
            @PathVariable Long recruitmentId,
            @RequestParam(defaultValue = "DOCUMENT") AdminStageFilter stage,
            @PageableDefault(size = 7) Pageable pageable
    ) {
        Page<ApplicationResponseDTO.SummaryForAdmin> page = applicationService.getByRecruitmentIdForAdmin(recruitmentId, stage, pageable);
        return SuccessResponse.ok(PagedResponse.from(page));
    }

    @PatchMapping("/status")
    @Operation(summary = "지원서 상태 일괄 수정", description = "지원서 ID 리스트와 변경할 상태를 받아 일괄 수정합니다.")
    public SuccessResponse<List<ApplicationResponseDTO.Summary>> updateStatus(@RequestBody @Valid ApplicationRequestDTO.UpdateStatus request) {
        List<ApplicationResponseDTO.Summary> updated = applicationService.updateStatus(request);
        return SuccessResponse.ok(updated);
    }

    @PostMapping("/distribute-evaluators")
    public SuccessResponse<String> distribute(
            @RequestBody @Valid ApplicationEvaluatorRequestDTO.Distribute request
    ) {
        applicationService.distributeEvaluators(request);
        return SuccessResponse.ok("성공했습니다.");
    }
}