package KUSITMS.WITHUS.domain.interview.timeslot.repository;

import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static KUSITMS.WITHUS.domain.interview.timeslot.entity.QTimeSlot.timeSlot;
import static KUSITMS.WITHUS.domain.interview.timeslotUser.entity.QTimeSlotUser.timeSlotUser;

@Service
@RequiredArgsConstructor
public class TimeSlotRepositoryImpl implements TimeSlotRepository {

    private final TimeSlotJpaRepository timeSlotJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public TimeSlot getById(Long id) {
        return timeSlotJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TIME_SLOT_NOT_EXIST));
    }

    @Override
    public TimeSlot save(TimeSlot timeSlot) {
        return timeSlotJpaRepository.save(timeSlot);
    }

    @Override
    public Optional<TimeSlot> findByDateTimeAndInterviewIdAndPosition(LocalDate date, LocalTime startTime, Long interviewId, Long positionId, String roomName) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(timeSlot.date.eq(date))
                .and(timeSlot.startTime.eq(startTime))
                .and(timeSlot.interview.id.eq(interviewId))
                .and(timeSlot.roomName.eq(roomName));

        if (positionId != null) {
            builder.and(timeSlot.position.id.eq(positionId));
        } else {
            builder.and(timeSlot.position.isNull());
        }

        return Optional.ofNullable(queryFactory
                .selectFrom(timeSlot)
                .where(builder)
                .fetchOne());
    }


    @Override
    public TimeSlot findOrCreate(LocalDate date, LocalTime startTime, LocalTime endTime, Interview interview, Position position, String roomName) {
        Long positionId = position != null ? position.getId() : null;
        return findByDateTimeAndInterviewIdAndPosition(date, startTime, interview.getId(), positionId, roomName)
                .orElseGet(() -> save(TimeSlot.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .interview(interview)
                        .position(position)
                        .roomName(roomName)
                        .build()));
    }

    @Override
    public List<TimeSlot> findByInterview(Interview interview) {
        return queryFactory
                .selectFrom(timeSlot)
                .where(timeSlot.interview.eq(interview))
                .orderBy(timeSlot.date.asc(), timeSlot.startTime.asc())
                .fetch();
    }

    @Override
    public List<TimeSlot> findAllByUserInvolved(Long interviewId, User user) {
        return queryFactory
                .selectFrom(timeSlot)
                .join(timeSlot.timeSlotUsers, timeSlotUser).fetchJoin()
                .where(
                        timeSlot.interview.id.eq(interviewId),
                        timeSlotUser.user.eq(user)
                )
                .orderBy(timeSlot.date.asc(), timeSlot.startTime.asc())
                .fetch();
    }
}
