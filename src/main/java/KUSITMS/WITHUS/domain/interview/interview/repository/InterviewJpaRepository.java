package KUSITMS.WITHUS.domain.interview.interview.repository;

import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewJpaRepository extends JpaRepository<Interview, Long> {
    Optional<Interview> findByRecruitmentId(Long recruitmentId);
}
