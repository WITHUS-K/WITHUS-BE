package KUSITMS.WITHUS.domain.interview.timeslotUser.service;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;

import java.util.List;

public interface TimeSlotUserService {

    void addUsersToTimeSlot(Long timeSlotId, List<Long> userIds, InterviewRole role);
}
