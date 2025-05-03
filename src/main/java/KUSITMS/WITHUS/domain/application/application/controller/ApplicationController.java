package KUSITMS.WITHUS.domain.application.application.controller;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.service.ApplicationService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public SuccessResponse<ApplicationResponseDTO.Detail> getById(@PathVariable Long id) {
        return SuccessResponse.ok(applicationService.getById(id));
    }

    @GetMapping("/recruitment/{recruitmentId}")
    @Operation(summary = "공고별 지원서 목록 조회", description = "공고 ID를 기준으로 전체 지원서 목록을 조회합니다.")
    public SuccessResponse<List<ApplicationResponseDTO.Summary>> getByRecruitment(@PathVariable Long recruitmentId) {
        return SuccessResponse.ok(applicationService.getByRecruitmentId(recruitmentId));
    }

    @PatchMapping("/status")
    @Operation(summary = "지원서 상태 일괄 수정", description = "지원서 ID 리스트와 변경할 상태를 받아 일괄 수정합니다.")
    public SuccessResponse<String> updateStatus(@RequestBody ApplicationRequestDTO.UpdateStatus request) {
        applicationService.updateStatus(request);
        return SuccessResponse.ok("지원서 상태가 성공적으로 수정되었습니다.");
    }
}

