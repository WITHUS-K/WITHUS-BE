package KUSITMS.WITHUS.domain.interview.interview.repository;

import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;

import java.util.List;
import java.util.Optional;

public interface InterviewRepository {
    Interview getById(Long interviewId);
    Interview save(Interview interview);
    List<Interview> findAllByRecruitmentIdIn(List<Long> recruitmentIds);
    Optional<Interview> findByRecruitmentId(Long id);
    void delete(Interview interview);
}
