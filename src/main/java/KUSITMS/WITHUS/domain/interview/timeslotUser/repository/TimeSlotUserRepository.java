package KUSITMS.WITHUS.domain.interview.timeslotUser.repository;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;

import java.util.List;

public interface TimeSlotUserRepository {
    void save(TimeSlotUser timeSlotUser);
    List<TimeSlotUser> findByTimeSlotId(Long timeSlotId);
    void deleteByInterviewIdAndRole(Long interviewId, InterviewRole role);
    List<Long> findMyTimeSlotIds(Long id, InterviewRole role);
    List<TimeSlotUser> findAllByTimeSlotIdInWithUser(List<Long> timeSlotIds);
}
