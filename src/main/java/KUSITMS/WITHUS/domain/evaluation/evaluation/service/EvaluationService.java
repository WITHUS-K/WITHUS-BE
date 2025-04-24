package KUSITMS.WITHUS.domain.evaluation.evaluation.service;

import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationResponseDTO;

public interface EvaluationService {
    EvaluationResponseDTO.Detail evaluate(EvaluationRequestDTO.Create request, Long userId);
}
