package KUSITMS.WITHUS.domain.recruitment.documentQuestion.repository;

import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DocumentQuestionRepositoryImpl implements DocumentQuestionRepository {

    private final DocumentQuestionJpaRepository documentQuestionJpaRepository;

    @Override
    public DocumentQuestion save(DocumentQuestion question) {
        return documentQuestionJpaRepository.save(question);
    }

    @Override
    public DocumentQuestion getById(Long questionId) {
        return documentQuestionJpaRepository.findById(questionId).orElseThrow(() -> new CustomException(ErrorCode.DOCUMENT_QUESTION_NOT_EXIST));
    }
}
