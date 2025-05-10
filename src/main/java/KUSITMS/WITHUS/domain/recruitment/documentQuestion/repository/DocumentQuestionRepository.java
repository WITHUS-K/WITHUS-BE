package KUSITMS.WITHUS.domain.recruitment.documentQuestion.repository;

import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;

import java.util.List;

public interface DocumentQuestionRepository {
    DocumentQuestion getById(Long questionId);
    List<DocumentQuestion> findByRecruitment(Recruitment recruitment);
}
