package KUSITMS.WITHUS.domain.interview.timeslot.repository;

import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeSlotJpaRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByInterview(Interview interview);
}
