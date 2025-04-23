package KUSITMS.WITHUS.domain.application.interviewQuestion.repository;

import KUSITMS.WITHUS.domain.application.interviewQuestion.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewQuestionJpaRepository extends JpaRepository<InterviewQuestion, Long> {
}
