package KUSITMS.WITHUS.domain.interview.interview.service;

import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;

public interface InterviewService {
    Long create(Long recruitmentId);
    Interview getById(Long interviewId);
}
