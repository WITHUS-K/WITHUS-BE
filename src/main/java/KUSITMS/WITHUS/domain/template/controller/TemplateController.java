package KUSITMS.WITHUS.domain.template.controller;

import KUSITMS.WITHUS.domain.template.dto.TemplateRequestDTO;
import KUSITMS.WITHUS.domain.template.dto.TemplateResponseDTO;
import KUSITMS.WITHUS.domain.template.enumerate.Medium;
import KUSITMS.WITHUS.domain.template.service.TemplateService;
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
    @Operation(summary = "문자/메일 템플릿 목록 조회", description = "내가 속한 조직에 등록된 템플릿의 요약 리스트(ID, 이름)를 반환합니다.")
    public SuccessResponse<List<TemplateResponseDTO.Summary>> list(
            @CurrentUser User user,
            @RequestParam Medium medium
    ) {
        List<TemplateResponseDTO.Summary> TemplatesList = templateService.listAll(medium, user);
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

    @PutMapping("/{templateId}")
    @Operation(summary = "문자/메일 템플릿 수정", description = "기존에 등록된 템플릿의 정보를 수정합니다. 자신이 속한 조직의 템플릿만 수정 가능합니다.")
    public SuccessResponse<TemplateResponseDTO.Detail> update(
            @PathVariable Long templateId,
            @RequestBody @Valid TemplateRequestDTO.Update dto,
            @CurrentUser User user
    ) {
        TemplateResponseDTO.Detail updated = templateService.update(templateId, dto, user);
        return SuccessResponse.ok(updated);
    }

    @DeleteMapping("/{templateId}")
    @Operation(summary = "문자/메일 템플릿 삭제", description = "등록된 템플릿을 삭제합니다. 자신이 속한 조직의 템플릿만 삭제 가능합니다.")
    public SuccessResponse<String> delete(
            @PathVariable Long templateId,
            @CurrentUser User user
    ) {
        templateService.delete(templateId, user);
        return SuccessResponse.ok("템플릿이 삭제되었습니다.");
    }

}

