package KUSITMS.WITHUS.domain.interview.interview.repository;

import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewJpaRepository extends JpaRepository<Interview, Long> {
}
