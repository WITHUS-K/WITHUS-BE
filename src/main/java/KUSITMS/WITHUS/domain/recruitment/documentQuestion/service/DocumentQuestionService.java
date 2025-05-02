package KUSITMS.WITHUS.domain.recruitment.documentQuestion.service;

import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionResponseDTO;

public interface DocumentQuestionService {
    DocumentQuestionResponseDTO.Create create(DocumentQuestionRequestDTO.Create request);
}
