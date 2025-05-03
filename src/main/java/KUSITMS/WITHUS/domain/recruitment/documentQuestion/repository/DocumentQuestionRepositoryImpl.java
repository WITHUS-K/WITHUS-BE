package KUSITMS.WITHUS.domain.recruitment.documentQuestion.repository;

import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.QDocumentQuestion.documentQuestion;

@Repository
@RequiredArgsConstructor
public class DocumentQuestionRepositoryImpl implements DocumentQuestionRepository {

    private final DocumentQuestionJpaRepository documentQuestionJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public DocumentQuestion save(DocumentQuestion question) {
        return documentQuestionJpaRepository.save(question);
    }

    @Override
    public DocumentQuestion getById(Long questionId) {
        return documentQuestionJpaRepository.findById(questionId).orElseThrow(() -> new CustomException(ErrorCode.DOCUMENT_QUESTION_NOT_EXIST));
    }

    @Override
    public List<DocumentQuestion> findByRecruitment(Recruitment recruitment) {
        return queryFactory.selectFrom(documentQuestion)
                .where(documentQuestion.recruitment.eq(recruitment))
                .fetch();
    }
}
