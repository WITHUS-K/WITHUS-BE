package KUSITMS.WITHUS.domain.application.application.controller;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.enumerate.EvaluationStatus;
import KUSITMS.WITHUS.domain.application.application.service.ApplicationService;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.common.annotation.CurrentUser;
import KUSITMS.WITHUS.global.response.PagedResponse;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "지원서 Controller")
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @RequestBody(content = @Content(
            encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "지원서 생성", description = "사용자로부터 받은 정보를 바탕으로 지원서를 생성합니다.")
    public SuccessResponse<ApplicationResponseDTO.Summary> create(
            @RequestPart(value = "request") @Valid ApplicationRequestDTO.Create request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        return SuccessResponse.ok(applicationService.create(request, profileImage, files));
    }

    @PostMapping("/bulk")
    @Operation(summary = "지원서 다건 생성", description = "여러 지원서 데이터를 한 번에 받아 모두 생성합니다.")
    public SuccessResponse<List<ApplicationResponseDTO.Summary>> createBulk(
            @Valid @RequestBody List<ApplicationRequestDTO.Create> requests
    ) {
        List<ApplicationResponseDTO.Summary> responses = requests.stream()
                .map(req -> applicationService.create(req, null, null))
                .toList();

        return SuccessResponse.ok(responses);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "지원서 삭제", description = "지원서 ID를 통해 해당 지원서를 삭제합니다.")
    public SuccessResponse<String> delete(@PathVariable Long id) {
        applicationService.delete(id);
        return SuccessResponse.ok("지원서 삭제에 성공하였습니다.");
    }

    @GetMapping("/{id}")
    @Operation(summary = "지원서 단건 조회", description = "지원서 ID를 기준으로 상세 정보를 조회합니다.")
    public SuccessResponse<ApplicationResponseDTO.Detail> getById(
            @PathVariable Long id,
            @CurrentUser User currentUser
    ) {
        return SuccessResponse.ok(applicationService.getById(id, currentUser.getId()));
    }

    @GetMapping("/recruitment/{recruitmentId}")
    @Operation(summary = "공고별 지원서 목록 조회", description = "공고 ID를 기준으로 전체 지원서 목록을 조회합니다.")
    public SuccessResponse<PagedResponse<ApplicationResponseDTO.SummaryForUser>> getByRecruitment(
            @PathVariable Long recruitmentId,
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "ALL") EvaluationStatus evaluationStatus,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 9) Pageable pageable
    ) {
        Page<ApplicationResponseDTO.SummaryForUser> page = applicationService.getByRecruitmentId(recruitmentId, currentUser.getId(), evaluationStatus, keyword, pageable);
        return SuccessResponse.ok(PagedResponse.from(page));
    }

    @PostMapping("/{applicationId}/acquaintance")
    public SuccessResponse<String> markAcquaintance(
            @PathVariable("applicationId") Long applicationId,
            @CurrentUser User currentUser
    ) {
        applicationService.markAcquaintance(applicationId, currentUser.getId());
        return SuccessResponse.ok("지인으로 표시되었습니다.");
    }

    @DeleteMapping("/{applicationId}/acquaintance")
    public SuccessResponse<String> unmarkAcquaintance(
            @PathVariable("applicationId") Long applicationId,
            @CurrentUser User currentUser
    ) {
        applicationService.unmarkAcquaintance(applicationId, currentUser.getId());
        return SuccessResponse.ok("지인 표시가 취소되었습니다.");
    }
}

