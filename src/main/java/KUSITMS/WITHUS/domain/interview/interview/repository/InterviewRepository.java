package KUSITMS.WITHUS.domain.interview.interview.repository;

import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;

public interface InterviewRepository {
    Interview getById(Long interviewId);
    Interview save(Interview interview);
}
