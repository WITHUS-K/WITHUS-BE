package KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.repository;

import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity.InterviewerAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewerAvailabilityJpaRepository extends JpaRepository<InterviewerAvailability, Long> {
    List<InterviewerAvailability> findByInterviewId(Long interviewId);
}
