package KUSITMS.WITHUS.domain.interview.timeslotUser.repository;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TimeSlotUserJpaRepository extends JpaRepository<TimeSlotUser, Long> {
    List<TimeSlotUser> findByTimeSlotId(Long timeSlotId);

    @Query("""
        select tsu
        from TimeSlotUser tsu
        join fetch tsu.user u
        join fetch tsu.timeSlot ts
        where tsu.timeSlot.id in :timeSlotIds
    """)
    List<TimeSlotUser> findAllByTimeSlotIdInWithUser(List<Long> timeSlotIds);

    @Query("""
        select distinct tsu.timeSlot.id
        from TimeSlotUser tsu
        where tsu.user.id = :userId
          and tsu.role = :role
    """)
    List<Long> findMyTimeSlotIds(Long userId, InterviewRole role);
}
