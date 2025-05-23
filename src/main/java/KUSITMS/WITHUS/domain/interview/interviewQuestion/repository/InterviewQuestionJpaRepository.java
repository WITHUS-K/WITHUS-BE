package KUSITMS.WITHUS.domain.interview.interviewQuestion.repository;

import KUSITMS.WITHUS.domain.interview.interviewQuestion.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewQuestionJpaRepository extends JpaRepository<InterviewQuestion, Long> {
}
