package KUSITMS.WITHUS.domain.evaluation.evaluation.controller;

import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.service.EvaluationReminderMailService;
import KUSITMS.WITHUS.domain.evaluation.evaluation.service.EvaluationService;
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
@Tag(name = "평가 Controller")
@RequestMapping("/api/v1/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final EvaluationReminderMailService evaluationReminderMailService;

    @PostMapping
    @Operation(summary = "지원서 평가 등록", description = "지원서에 대해 평가 기준별 점수를 등록합니다.")
    public SuccessResponse<EvaluationResponseDTO.Detail> evaluate(
            @RequestBody @Valid EvaluationRequestDTO.Create request,
            @CurrentUser User currentUser
    ) {
        EvaluationResponseDTO.Detail response = evaluationService.evaluate(request, currentUser.getId());
        return SuccessResponse.ok(response);
    }

    @PostMapping("/bulk")
    @Operation(summary = "여러 평가 기준에 대해 한 번에 평가 등록", description = "지원서에 대해 여러 개의 평가 기준을 기반으로 평가 점수를 한 번에 등록합니다.")
    public SuccessResponse<List<EvaluationResponseDTO.Detail>> bulkEvaluate(
            @Valid @RequestBody EvaluationRequestDTO.BulkCreate request,
            @CurrentUser User currentUser
    ) {
        List<EvaluationResponseDTO.Detail> response = evaluationService.bulkEvaluate(request, currentUser.getId());
        return SuccessResponse.ok(response);
    }


    @PostMapping("/remind")
    @Operation(summary = "평가 리마인드 메일 전송", description = "평가 마감일과 평가 링크를 포함하여 미완료자들에게 리마인드 메일을 발송합니다.")
    public SuccessResponse<String> sendEvaluationReminder(
            @RequestParam Long recruitmentId,
            @RequestBody @Valid EvaluationRequestDTO.Reminder request
    ) {
        evaluationReminderMailService.sendEvaluationReminderMails(recruitmentId, request.userIds());
        return SuccessResponse.ok("평가 미완료자에게 리마인드 메일이 전송되었습니다.");
    }

}
