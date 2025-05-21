package KUSITMS.WITHUS.domain.template.controller;

import KUSITMS.WITHUS.domain.template.dto.TemplateRequestDTO;
import KUSITMS.WITHUS.domain.template.dto.TemplateResponseDTO;
import KUSITMS.WITHUS.domain.template.enumerate.TemplateType;
import KUSITMS.WITHUS.domain.template.service.TemplateService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "템플릿 Controller")
@RequestMapping("/api/v1/templates")
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping("/{templateId}")
    @Operation(summary = "문자/메일 템플릿 개별 조회", description = "등록된 템플릿의 상세 정보를 반환합니다.")
    public SuccessResponse<TemplateResponseDTO.Detail> getById(
            @PathVariable Long templateId
    ) {
        TemplateResponseDTO.Detail template = templateService.getById(templateId);
        return SuccessResponse.ok(template);
    }

    @GetMapping
    @Operation(summary = "문자/메일 템플릿 목록 조회", description = "등록된 템플릿의 요약 리스트(ID, 이름)를 반환합니다.")
    public SuccessResponse<List<TemplateResponseDTO.Summary>> list(
            @RequestParam TemplateType templateType
            ) {
        List<TemplateResponseDTO.Summary> TemplatesList = templateService.listAll(templateType);
        return SuccessResponse.ok(TemplatesList);
    }

    @PostMapping
    @Operation(summary = "문자/메일 템플릿 생성", description = "새 메일 템플릿 이름, 제목, 본문을 받아 저장하고 상세 정보를 반환합니다.")
    public SuccessResponse<TemplateResponseDTO.Detail> create(
            @RequestBody @Valid TemplateRequestDTO.Create dto
    ) {
        TemplateResponseDTO.Detail created = templateService.create(dto);
        return SuccessResponse.ok(created);
    }

}

