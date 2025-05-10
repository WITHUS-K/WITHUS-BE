package KUSITMS.WITHUS.domain.interview.timeslotUser.service;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;

import java.util.List;

public interface TimeSlotUserService {
    void addUsersToTimeSlot(Long timeSlotId, List<Long> userIds, InterviewRole role);
    List<TimeSlotUser> getUsersByTimeSlot(Long timeSlotId);
}
