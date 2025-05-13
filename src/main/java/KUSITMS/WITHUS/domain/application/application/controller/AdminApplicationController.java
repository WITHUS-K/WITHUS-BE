package KUSITMS.WITHUS.domain.application.application.controller;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.enumerate.AdminApplicationSortField;
import KUSITMS.WITHUS.domain.application.application.enumerate.AdminStageFilter;
import KUSITMS.WITHUS.domain.application.application.service.ApplicationMailService;
import KUSITMS.WITHUS.domain.application.application.service.ApplicationService;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.dto.ApplicationEvaluatorRequestDTO;
import KUSITMS.WITHUS.global.response.PagedResponse;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "관리자용 지원서 Controller")
@RequestMapping("/api/v1/admin/applications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApplicationController {

    private final ApplicationService applicationService;
    private final ApplicationMailService mailService;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @GetMapping("/recruitment/{recruitmentId}")
    @Operation(summary = "관리자용 지원서 목록 조회", description = "공고별 지원서를 단계(stage), 정렬(sortBy, direction), 페이지(page, size) 옵션으로 조회합니다.")
    public SuccessResponse<PagedResponse<ApplicationResponseDTO.SummaryForAdmin>> getList(
            @PathVariable Long recruitmentId,
            @RequestParam(defaultValue = "DOCUMENT") AdminStageFilter stage,
            @RequestParam(defaultValue = "NAME") AdminApplicationSortField sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @PageableDefault(size = 7) Pageable pageable
    ) {
        Page<ApplicationResponseDTO.SummaryForAdmin> page = applicationService.getByRecruitmentIdForAdmin(recruitmentId, stage, pageable, sortBy, direction);
        return SuccessResponse.ok(PagedResponse.from(page));
    }

    @PatchMapping("/status")
    @Operation(summary = "지원서 상태 일괄 수정", description = "지원서 ID 리스트와 변경할 상태를 받아 일괄 수정합니다.")
    public SuccessResponse<List<ApplicationResponseDTO.Summary>> updateStatus(@RequestBody @Valid ApplicationRequestDTO.UpdateStatus request) {
        List<ApplicationResponseDTO.Summary> updated = applicationService.updateStatus(request);
        return SuccessResponse.ok(updated);
    }

    @PostMapping("/distribute-evaluators")
    @Operation(summary = "지원서 평가 담당자 분배", description = "대상 공고 Id와 지원 파트 Id, 평가 담당자 역할 Id, 지원서 당 배정할 평가 담당자 수를 받아 랜덤 배정합니다.")
    public SuccessResponse<String> distribute(
            @RequestBody @Valid ApplicationEvaluatorRequestDTO.Distribute request
    ) {
        applicationService.distributeEvaluators(request);
        return SuccessResponse.ok("성공했습니다.");
    }

    @PostMapping("/evaluators")
    @Operation(summary = "지원서별 평가 담당자 업데이트", description = "기존 평가자는 모두 지우고, 주어진 userId 리스트로 재배정합니다.")
    public SuccessResponse<String> updateEvaluators(
            @RequestBody @Valid ApplicationEvaluatorRequestDTO.Update request
    ) {
        applicationService.updateEvaluators(request);
        return SuccessResponse.ok("성공했습니다.");
    }

    @PostMapping(
            path = "/bulk-mail",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "지원서 대상 다중 메일 발송", description = "복수의 수신자 이메일, 제목, 본문, 첨부파일을 받아 일괄 메일을 발송합니다.")
    public SuccessResponse<String> sendBulkMail(
            @ModelAttribute @Valid ApplicationRequestDTO.SendBulkMail request
    ) throws MessagingException {
        mailService.sendBulkMail(
                request.recipients(),
                request.subject(),
                request.body(),
                request.attachments()
        );
        return SuccessResponse.ok("메일을 발송 요청을 보냈습니다.");
    }

}