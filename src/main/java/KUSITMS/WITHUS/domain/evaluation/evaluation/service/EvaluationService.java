package KUSITMS.WITHUS.domain.evaluation.evaluation.service;

import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface EvaluationService {
    EvaluationResponseDTO.Detail evaluate(EvaluationRequestDTO.Create request, Long userId);
    List<EvaluationResponseDTO.Detail> bulkEvaluate(EvaluationRequestDTO.@Valid BulkCreate request, Long id);
}
