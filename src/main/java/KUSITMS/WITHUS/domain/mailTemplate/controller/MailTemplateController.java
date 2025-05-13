package KUSITMS.WITHUS.domain.mailTemplate.controller;

import KUSITMS.WITHUS.domain.mailTemplate.dto.MailTemplateRequestDTO;
import KUSITMS.WITHUS.domain.mailTemplate.dto.MailTemplateResponseDTO;
import KUSITMS.WITHUS.domain.mailTemplate.service.MailTemplateService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "메일 템플릿 Controller")
@RequestMapping("/api/v1/mail/templates")
public class MailTemplateController {

    private final MailTemplateService mailTemplateService;

    @GetMapping
    @Operation(summary = "메일 템플릿 목록 조회", description = "등록된 메일 템플릿의 요약 리스트(ID, 이름)를 반환합니다.")
    public SuccessResponse<List<MailTemplateResponseDTO.Summary>> list() {
        List<MailTemplateResponseDTO.Summary> mailTemplatesList = mailTemplateService.listAll();
        return SuccessResponse.ok(mailTemplatesList);
    }

    @PostMapping
    @Operation(summary = "메일 템플릿 생성", description = "새 메일 템플릿 이름, 제목, 본문을 받아 저장하고 상세 정보를 반환합니다.")
    public SuccessResponse<MailTemplateResponseDTO.Detail> create(
            @RequestBody @Valid MailTemplateRequestDTO.Create dto
    ) {
        MailTemplateResponseDTO.Detail created = mailTemplateService.create(dto);
        return SuccessResponse.ok(created);
    }

}

