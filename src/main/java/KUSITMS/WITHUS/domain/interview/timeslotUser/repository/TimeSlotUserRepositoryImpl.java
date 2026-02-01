package KUSITMS.WITHUS.domain.interview.timeslotUser.repository;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.interview.timeslotUser.entity.QTimeSlotUser.timeSlotUser;

@Repository
@RequiredArgsConstructor
public class TimeSlotUserRepositoryImpl implements TimeSlotUserRepository {

    private final TimeSlotUserJpaRepository timeSlotUserJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public void save(TimeSlotUser timeSlotUser) {
        timeSlotUserJpaRepository.save(timeSlotUser);
    }

    @Override
    public List<TimeSlotUser> findByTimeSlotId(Long timeSlotId) {
        return  timeSlotUserJpaRepository.findByTimeSlotId(timeSlotId);
    }

    @Override
    public void deleteByInterviewIdAndRole(Long interviewId, InterviewRole role) {
        queryFactory.delete(timeSlotUser)
                .where(
                        timeSlotUser.timeSlot.interview.id.eq(interviewId),
                        timeSlotUser.role.eq(role)
                )
                .execute();
    }

    @Override
    public List<Long> findMyTimeSlotIds(Long userId, InterviewRole role) {
        return queryFactory
                .select(timeSlotUser.timeSlot.id)
                .from(timeSlotUser)
                .where(
                        timeSlotUser.user.id.eq(userId),
                        timeSlotUser.role.eq(role)
                )
                .distinct()
                .fetch();
    }

    @Override
    public List<TimeSlotUser> findAllByTimeSlotIdInWithUser(List<Long> timeSlotIds) {
        return queryFactory
                .selectFrom(timeSlotUser)
                .distinct()
                .join(timeSlotUser.user).fetchJoin()
                .join(timeSlotUser.timeSlot).fetchJoin()
                .where(timeSlotUser.timeSlot.id.in(timeSlotIds))
                .fetch();

    }
}
