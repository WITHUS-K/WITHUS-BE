package KUSITMS.WITHUS.domain.evaluation.evaluation.controller;

import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.service.EvaluationService;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.common.annotation.CurrentUser;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "평가 Controller")
@RequestMapping("/api/v1/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping
    @Operation(summary = "지원서 평가 등록", description = "지원서에 대해 평가 기준별 점수를 등록합니다.")
    public SuccessResponse<EvaluationResponseDTO.Detail> evaluate(
            @Valid @RequestBody EvaluationRequestDTO.Create request,
            @CurrentUser User currentUser
    ) {
        EvaluationResponseDTO.Detail response = evaluationService.evaluate(request, currentUser.getId());
        return SuccessResponse.ok(response);
    }
}
