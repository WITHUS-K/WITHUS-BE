package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.controller;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.service.EvaluationCriteriaService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "평가 기준 Controller")
@RequestMapping("/api/v1/recruitments/{recruitmentId}/criteria")
public class EvaluationCriteriaController {

    private final EvaluationCriteriaService criteriaService;

    @PostMapping
    @Operation(summary = "평가 기준 생성", description = "리크루팅 ID를 기반으로 기준 내용, 타입(서류, 면접)을 받아서 평가 기준을 생성합니다.")
    public SuccessResponse<EvaluationCriteriaResponseDTO.Create> addCriteria(
            @PathVariable Long recruitmentId,
            @RequestBody @Valid EvaluationCriteriaRequestDTO.Create request
    ) {
        EvaluationCriteriaResponseDTO.Create response = criteriaService.addCriteria(recruitmentId, request);
        return SuccessResponse.ok(response);
    }
}
