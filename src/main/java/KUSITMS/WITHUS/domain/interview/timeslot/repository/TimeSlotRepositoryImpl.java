package KUSITMS.WITHUS.domain.interview.timeslot.repository;

import KUSITMS.WITHUS.domain.application.position.entity.Position;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static KUSITMS.WITHUS.domain.interview.timeslot.entity.QTimeSlot.timeSlot;

@Service
@RequiredArgsConstructor
public class TimeSlotRepositoryImpl implements TimeSlotRepository {

    private final TimeSlotJpaRepository timeSlotJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public TimeSlot getById(Long id) {
        return timeSlotJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TIME_SLOT_ALREADY_EXIST));
    }

    @Override
    public TimeSlot save(TimeSlot timeSlot) {
        return timeSlotJpaRepository.save(timeSlot);
    }

    @Override
    public Optional<TimeSlot> findByDateTimeAndInterviewIdAndPosition(LocalDate date, LocalTime startTime, Long interviewId, Long positionId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(timeSlot)
                .where(
                        timeSlot.date.eq(date),
                        timeSlot.startTime.eq(startTime),
                        timeSlot.interview.id.eq(interviewId),
                        timeSlot.position.id.eq(positionId)
                )
                .fetchOne());
    }

    @Override
    public TimeSlot findOrCreate(LocalDate date, LocalTime startTime, LocalTime endTime, Interview interview, Position position) {
        return findByDateTimeAndInterviewIdAndPosition(date, startTime, interview.getId(), position.getId())
                .orElseGet(() -> save(TimeSlot.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .interview(interview)
                        .position(position)
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
}
