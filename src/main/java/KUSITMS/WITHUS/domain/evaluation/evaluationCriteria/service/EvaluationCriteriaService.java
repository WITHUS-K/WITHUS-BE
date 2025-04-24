package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.service;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaResponseDTO;

public interface EvaluationCriteriaService {
    EvaluationCriteriaResponseDTO.Create addCriteria(Long recruitmentId, EvaluationCriteriaRequestDTO.Create request);
}
