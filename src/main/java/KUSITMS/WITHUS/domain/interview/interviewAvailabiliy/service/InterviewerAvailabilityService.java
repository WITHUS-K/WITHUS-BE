package KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.service;

import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity.InterviewerAvailability;
import KUSITMS.WITHUS.domain.user.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface InterviewerAvailabilityService {
    List<InterviewerAvailability> registerAvailability(Long interviewId, User user, List<LocalDateTime> times);
}
