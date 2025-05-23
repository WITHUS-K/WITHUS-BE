package KUSITMS.WITHUS.domain.interview.interviewQuestion.service;

import KUSITMS.WITHUS.domain.interview.interviewQuestion.entity.InterviewQuestion;

public interface InterviewQuestionService {
    InterviewQuestion addQuestionToApplication(Long applicationId, Long userId, String content);
    InterviewQuestion updateQuestion(Long questionId, Long id, String content);
}
