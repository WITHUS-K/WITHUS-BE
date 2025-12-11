package KUSITMS.WITHUS.domain.interview.timeslot.repository;

import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.user.user.entity.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository {
    TimeSlot getById(Long id);
    TimeSlot save(TimeSlot timeSlot);
    Optional<TimeSlot> findByDateTimeAndInterviewIdAndPosition(LocalDate date, LocalTime startTime, Long interviewId, Long organizationRoleId, String roomName);
    TimeSlot findOrCreate(LocalDate date, LocalTime startTime, LocalTime endTime, Interview interview, OrganizationRole organizationRole, String roomName);
    List<TimeSlot> findByInterviewId(Long interviewId);
    List<TimeSlot> findAllByUserInvolved(Long interviewId, User user);
    void deleteAllByInterview(Long interviewId);
    void deleteAll(List<TimeSlot> oldSlots);
}
