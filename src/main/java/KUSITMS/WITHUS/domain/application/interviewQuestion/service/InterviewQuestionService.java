package KUSITMS.WITHUS.domain.application.interviewQuestion.service;

import KUSITMS.WITHUS.domain.application.interviewQuestion.entity.InterviewQuestion;

public interface InterviewQuestionService {
    InterviewQuestion addQuestionToApplication(Long applicationId, Long userId, String content);
    InterviewQuestion updateQuestion(Long questionId, Long id, String content);
}
