package KUSITMS.WITHUS.domain.recruitment.recruitment.controller;

import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.RecruitmentService;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.common.annotation.CurrentUser;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "관리자용 공고 Controller")
@RequestMapping("/api/v1/admin/recruitments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRecruitmentController {

    private final RecruitmentService recruitmentService;

    @GetMapping("/{recruitmentId}/summary")
    @Operation(summary = "리크루팅 단건 요약 조회")
    public SuccessResponse<RecruitmentResponseDTO.Summary> getSummary(@PathVariable Long recruitmentId) {
        RecruitmentResponseDTO.Summary result = recruitmentService.getByIdAs(recruitmentId, RecruitmentResponseDTO.Summary::from);
        return SuccessResponse.ok(result);
    }

    @GetMapping("/current/summary")
    @Operation(summary = "내 조직의 현재 진행 중인 공고 요약 조회", description = "로그인한 운영진이 속한 조직의, 공고 생성일이 지났고 최종 발표일 +1일 이전인 공고들의 요약 정보를 반환합니다.")
    public SuccessResponse<List<RecruitmentResponseDTO.SummaryForHome>> getCurrentSummaries(
            @CurrentUser User currentUser
    ) {
        List<RecruitmentResponseDTO.SummaryForHome> dtos =
                recruitmentService.getCurrentSummariesForAdmin(currentUser.getId());
        return SuccessResponse.ok(dtos);
    }
}
