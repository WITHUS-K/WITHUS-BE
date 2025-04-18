package KUSITMS.WITHUS.domain.application.application.controller;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.service.ApplicationService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "지원서 Controller")
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "지원서 생성", description = "사용자로부터 받은 정보를 바탕으로 지원서를 생성합니다.")
    public SuccessResponse<ApplicationResponseDTO.Detail> create(
            @Valid @RequestBody ApplicationRequestDTO.Create request
    ) {
        return SuccessResponse.ok(applicationService.create(request));
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
}

