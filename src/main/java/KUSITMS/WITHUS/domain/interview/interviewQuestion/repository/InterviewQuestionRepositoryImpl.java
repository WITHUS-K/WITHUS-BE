package KUSITMS.WITHUS.domain.interview.interviewQuestion.repository;

import KUSITMS.WITHUS.domain.interview.interviewQuestion.entity.InterviewQuestion;
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
}
