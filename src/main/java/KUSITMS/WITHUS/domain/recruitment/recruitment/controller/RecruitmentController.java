package KUSITMS.WITHUS.domain.recruitment.recruitment.controller;

import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.service.RecruitmentService;
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
@Tag(name = "공고(리크루팅) Controller")
@RequestMapping("/api/v1/recruitments")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    @PostMapping("/draft")
    @Operation(summary = "리크루팅 임시 저장", description = "임시 저장 상태의 공고를 생성하거나 수정합니다. recruitmentId가 존재하면 수정, null이면 생성합니다.")
    public SuccessResponse<RecruitmentResponseDTO.Create> saveDraft(
            @Valid @RequestBody RecruitmentRequestDTO.Upsert request
    ) {
        RecruitmentResponseDTO.Create result = recruitmentService.saveDraft(request);
        return SuccessResponse.ok(result);
    }

    @PostMapping("/publish")
    @Operation(summary = "리크루팅 최종 저장", description = "최종 저장 상태의 공고를 생성하거나 임시 저장된 공고를 최종 저장 상태로 저장합니다. recruitmentId가 존재하면 수정, null이면 생성합니다.")
    public SuccessResponse<RecruitmentResponseDTO.Create> publish(
            @Valid @RequestBody RecruitmentRequestDTO.Upsert request
    ) {
        RecruitmentResponseDTO.Create result = recruitmentService.publish(request);
        return SuccessResponse.ok(result);
    }

    @GetMapping("/{recruitmentId}")
    @Operation(summary = "리크루팅 단건 상세 조회")
    public SuccessResponse<RecruitmentResponseDTO.Detail> getById(@PathVariable("recruitmentId") Long recruitmentId) {
        RecruitmentResponseDTO.Detail result = recruitmentService.getById(recruitmentId);
        return SuccessResponse.ok(result);
    }

    @PutMapping("/{recruitmentId}")
    @Operation(summary = "리크루팅 수정")
    public SuccessResponse<RecruitmentResponseDTO.Update> update(
            @PathVariable("recruitmentId") Long id,
            @Valid @RequestBody RecruitmentRequestDTO.Update request
    ) {
        RecruitmentResponseDTO.Update result = recruitmentService.update(id, request);
        return SuccessResponse.ok(result);
    }

    @DeleteMapping("/{recruitmentId}")
    @Operation(summary = "리크루팅 삭제")
    public SuccessResponse<String> delete(@PathVariable("recruitmentId") Long id) {
        recruitmentService.delete(id);
        return SuccessResponse.ok("리크루팅 삭제에 성공하였습니다.");
    }

    @GetMapping
    @Operation(summary = "리크루팅 목록 조회 및 검색", description = "공고의 title을 기준으로 keyword를 검색합니다. keyword가 없으면 전체 조회합니다.")
    public SuccessResponse<List<RecruitmentResponseDTO.Summary>> getAllByKeyword(
            @RequestParam(required = false) String keyword
    ) {
        List<RecruitmentResponseDTO.Summary> result = recruitmentService.getAllByKeyword(keyword);
        return SuccessResponse.ok(result);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "슬러그 기반 리크루팅 상세 조회", description = "slug를 통해 공고 상세 정보를 조회합니다.")
    public SuccessResponse<RecruitmentResponseDTO.Detail> getBySlug(@PathVariable("slug") String slug) {
        RecruitmentResponseDTO.Detail result = recruitmentService.getBySlug(slug);
        return SuccessResponse.ok(result);
    }

    @GetMapping("/my-organizations")
    @Operation(summary = "내가 속한 조직의 모든 리크루팅 목록 조회")
    public SuccessResponse<List<RecruitmentResponseDTO.Simple>> getAllMyOrganizationRecruitments(@CurrentUser User user) {
        List<RecruitmentResponseDTO.Simple> result = recruitmentService.getAllByUserOrganizations(user);
        return SuccessResponse.ok(result);
    }

    @GetMapping("/{organizationId}/current/summary")
    @Operation(summary = "내 조직의 현재 진행 중인 공고 요약 조회", description = "로그인한 운영진이 속한 조직의, 공고 생성일이 지났고 최종 발표일 +1일 이전인 공고들의 요약 정보를 반환합니다.")
    public SuccessResponse<List<RecruitmentResponseDTO.SummaryForHome>> getCurrentSummaries(
            @PathVariable("organizationId") Long organizationId,
            @CurrentUser User currentUser
    ) {
        List<RecruitmentResponseDTO.SummaryForHome> dtos =
                recruitmentService.getCurrentSummariesForUser(organizationId, currentUser.getId());
        return SuccessResponse.ok(dtos);
    }

    @GetMapping("/{recruitmentId}/my/evaluations/documents")
    @Operation(summary = "내 서류 평가 현황 조회", description = "내가 할당된 서류 평가 지원서 중, 미완료/완료 리스트를 반환합니다.")
    public SuccessResponse<RecruitmentResponseDTO.MyDocumentEvaluation> myDocs(
            @CurrentUser User currentUser,
            @PathVariable("recruitmentId") Long recruitmentId
    ) {
        var response = recruitmentService.getMyDocumentEvaluations(currentUser.getId(), recruitmentId);
        return SuccessResponse.ok(response);
    }

}
