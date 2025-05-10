package KUSITMS.WITHUS.domain.interview.timeslotUser.repository;

import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeSlotUserRepository extends JpaRepository<TimeSlotUser, Long> {
    List<TimeSlotUser> findByTimeSlotId(Long timeSlotId);
}
