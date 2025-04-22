package KUSITMS.WITHUS.domain.interview.timeslot.repository;

import KUSITMS.WITHUS.domain.application.position.entity.Position;
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
    Optional<TimeSlot> findByDateTimeAndInterviewIdAndPosition(LocalDate date, LocalTime startTime, Long interviewId, Long positionId);
    TimeSlot findOrCreate(LocalDate date, LocalTime startTime, LocalTime endTime, Interview interview, Position position);
    List<TimeSlot> findByInterview(Interview interview);
    List<TimeSlot> findAllByUserInvolved(Long interviewId, User user);
}
