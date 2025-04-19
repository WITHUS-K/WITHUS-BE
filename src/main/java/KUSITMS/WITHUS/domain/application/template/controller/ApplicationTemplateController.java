package KUSITMS.WITHUS.domain.application.template.controller;

import KUSITMS.WITHUS.domain.application.template.dto.ApplicationTemplateRequestDTO;
import KUSITMS.WITHUS.domain.application.template.dto.ApplicationTemplateResponseDTO;
import KUSITMS.WITHUS.domain.application.template.service.ApplicationTemplateService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "지원서 양식 Controller")
@RequestMapping("api/v1/application-templates")
public class ApplicationTemplateController {

    private final ApplicationTemplateService applicationTemplateService;

    @PostMapping
    @Operation(summary = "지원서 양식 생성", description = "리크루팅 ID를 기반으로 지원서 양식을 생성합니다.")
    public SuccessResponse<ApplicationTemplateResponseDTO.Detail> create(
            @Valid @RequestBody ApplicationTemplateRequestDTO.Create request
    ) {
        return SuccessResponse.ok(applicationTemplateService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "지원서 양식 단건 조회", description = "ID를 기반으로 지원서 양식 정보를 조회합니다.")
    public SuccessResponse<ApplicationTemplateResponseDTO.Detail> getById(@PathVariable Long id) {
        return SuccessResponse.ok(applicationTemplateService.getById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "지원서 양식 삭제", description = "ID를 기준으로 지원서 양식을 삭제합니다.")
    public SuccessResponse<String> delete(@PathVariable Long id) {
        applicationTemplateService.delete(id);
        return SuccessResponse.ok("지원서 양식 삭제에 성공하였습니다.");
    }
}
