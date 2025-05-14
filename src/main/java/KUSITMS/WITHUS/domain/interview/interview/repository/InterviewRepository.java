package KUSITMS.WITHUS.domain.interview.interview.repository;

import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;

import java.util.List;

public interface InterviewRepository {
    Interview getById(Long interviewId);
    Interview save(Interview interview);
    List<Interview> findAllByRecruitmentIdIn(List<Long> recruitmentIds);
}
