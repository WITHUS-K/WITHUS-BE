package KUSITMS.WITHUS.domain.interview.interviewQuestion.repository;

import KUSITMS.WITHUS.domain.interview.interviewQuestion.entity.InterviewQuestion;

public interface InterviewQuestionRepository {
    InterviewQuestion save(InterviewQuestion interviewQuestion);
    InterviewQuestion getById(Long questionId);
}
