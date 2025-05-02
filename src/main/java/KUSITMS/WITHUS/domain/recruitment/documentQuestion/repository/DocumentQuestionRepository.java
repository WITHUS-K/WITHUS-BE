package KUSITMS.WITHUS.domain.recruitment.documentQuestion.repository;

import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;

public interface DocumentQuestionRepository {
    DocumentQuestion save(DocumentQuestion question);
    DocumentQuestion getById(Long questionId);
}
