package KUSITMS.WITHUS.domain.interview.timeslot.repository;

import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeSlotJpaRepository extends JpaRepository<TimeSlot, Long> {
    void deleteByInterviewId(Long interviewId);

    @EntityGraph(attributePaths = {
            "interview",
            "interview.recruitment",
            "applications"
    })
    List<TimeSlot> findAllByIdIn(List<Long> ids);
}
