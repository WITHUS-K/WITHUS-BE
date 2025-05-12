package KUSITMS.WITHUS.domain.application.application.controller;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.enumerate.AdminStageFilter;
import KUSITMS.WITHUS.domain.application.application.enumerate.EvaluationStatus;
import KUSITMS.WITHUS.domain.application.application.service.ApplicationService;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.common.annotation.CurrentUser;
import KUSITMS.WITHUS.global.response.PagedResponse;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}