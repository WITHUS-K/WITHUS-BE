package KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.repository;

import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity.InterviewerAvailability;

import java.util.List;

public interface InterviewerAvailabilityRepository {
    List<InterviewerAvailability> saveAll(List<InterviewerAvailability> availabilities);
}
