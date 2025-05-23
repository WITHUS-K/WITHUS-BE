package KUSITMS.WITHUS.domain.interview.interviewQuestion.repository;

import KUSITMS.WITHUS.domain.interview.interviewQuestion.entity.InterviewQuestion;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InterviewQuestionRepositoryImpl implements InterviewQuestionRepository {

    private final InterviewQuestionJpaRepository interviewQuestionJpaRepository;

    @Override
    public InterviewQuestion save(InterviewQuestion interviewQuestion) {
        return interviewQuestionJpaRepository.save(interviewQuestion);
    }

    @Override
    public InterviewQuestion getById(Long questionId) {
        return interviewQuestionJpaRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_QUESTION_NOT_EXIST));
    }
}
