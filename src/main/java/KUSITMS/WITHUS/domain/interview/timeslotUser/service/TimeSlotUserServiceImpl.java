package KUSITMS.WITHUS.domain.interview.timeslotUser.service;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.interview.repository.InterviewRepository;
import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity.InterviewerAvailability;
import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.repository.InterviewerAvailabilityRepository;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslot.repository.TimeSlotRepository;
import KUSITMS.WITHUS.domain.interview.timeslot.service.util.*;
import KUSITMS.WITHUS.domain.interview.timeslot.service.util.calendar.BusyCalendar;
import KUSITMS.WITHUS.domain.interview.timeslot.service.util.calendar.InMemoryBusyCalendar;
import KUSITMS.WITHUS.domain.interview.timeslot.service.util.candidate.CandidateIndex;
import KUSITMS.WITHUS.domain.interview.timeslot.service.util.candidate.CandidateSelector;
import KUSITMS.WITHUS.domain.interview.timeslot.service.util.selection.policy.FairnessPolicy;
import KUSITMS.WITHUS.domain.interview.timeslot.service.util.selection.ordering.SlotOrderingStrategy;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import KUSITMS.WITHUS.domain.interview.timeslotUser.repository.TimeSlotUserRepository;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TimeSlotUserServiceImpl implements TimeSlotUserService {

    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final TimeSlotUserRepository timeSlotUserRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewerAvailabilityRepository interviewerAvailabilityRepository;

    private final SlotOrderingStrategy slotOrderingStrategy;
    private final CandidateSelector candidateSelector;
    private final FairnessPolicy fairnessPolicy;
    private final AssignmentRecorder assignmentRecorder;

    @Override
    @Transactional
    public void addUsersToTimeSlot(Long timeSlotId, List<Long> userIds, InterviewRole role) {
        TimeSlot timeSlot = timeSlotRepository.getById(timeSlotId);
        List<User> users = userRepository.findAllById(userIds);

        for (User user : users) {
            TimeSlotUser timeSlotUser = TimeSlotUser.builder()
                    .role(role)
                    .build();

            timeSlotUser.assignTimeSlot(timeSlot);
            timeSlotUser.assignUser(user);

            timeSlot.addTimeSlotUser(timeSlotUser);
            user.addTimeSlotUser(timeSlotUser);
        }
    }

    @Override
    public List<TimeSlotUser> getUsersByTimeSlot(Long timeSlotId) {
        return timeSlotUserRepository.findByTimeSlotId(timeSlotId);
    }

    @Override
    @Transactional
    public void updateUsersInTimeSlot(Long timeSlotId, List<Long> requestedUserIds, InterviewRole role) {
        TimeSlot timeSlot = timeSlotRepository.getById(timeSlotId);

        // 현재 DB에 존재하는 사용자 ID
        Map<Long, TimeSlotUser> existingUserMap = timeSlot.getTimeSlotUsers().stream()
                .filter(u -> u.getRole() == role)
                .collect(Collectors.toMap(
                        u -> u.getUser().getId(),
                        Function.identity()
                ));

        Set<Long> requestedSet = new HashSet<>(requestedUserIds);

        // 삭제 대상 - DB에는 있었는데 요청에는 없는 ID
        for (Long existingId : existingUserMap.keySet()) {
            if (!requestedSet.contains(existingId)) {
                timeSlot.getTimeSlotUsers().remove(existingUserMap.get(existingId));
            }
        }

        // 추가 대상 - 요청에는 있는데 DB에 없는 ID
        List<Long> toAdd = requestedUserIds.stream()
                .filter(id -> !existingUserMap.containsKey(id))
                .toList();

        List<User> usersToAdd = userRepository.findAllById(toAdd);
        for (User user : usersToAdd) {
            TimeSlotUser newRelation = TimeSlotUser.builder()
                    .timeSlot(timeSlot)
                    .user(user)
                    .role(role)
                    .build();
            timeSlot.getTimeSlotUsers().add(newRelation);
        }
    }

    /**
     * 면접관 자동 배정 오케스트레이션
     */
    @Override
    @Transactional
    public void assignInterviewers(final Long interviewId) {
        timeSlotUserRepository.deleteByInterviewIdAndRole(interviewId, InterviewRole.INTERVIEWER);

        final Interview interview = interviewRepository.getById(interviewId);
        final List<TimeSlot> timeSlots = timeSlotRepository.findByInterviewId(interviewId);
        final List<InterviewerAvailability> availabilities = interviewerAvailabilityRepository.findByInterviewId(interviewId);

        log.info("[1] 타임슬롯 수: {}", timeSlots.size());
        log.info("[2] 인터뷰어 가능 시간 제출 수: {}", availabilities.size());

        final CandidateIndex index = CandidateIndex.of(availabilities, userRepository);

        log.info("[3] 가능한 유저 수: {}", index.getAvailableTimes().size());

        final Map<Long, Integer> assignedCount = initAssignedCount(index.getUserMap().keySet());
        final BusyCalendar busyCalendar = new InMemoryBusyCalendar(index.getUserMap().keySet());

        final List<TimeSlot> orderedSlots = slotOrderingStrategy.order(timeSlots, index);

        final int needPerSlot = resolveNeedPerSlot(interview);
        for (final TimeSlot slot : orderedSlots) {
            processSlot(slot, needPerSlot, index, assignedCount, busyCalendar);
        }
    }

    /** 공평 분배 카운터 초기화 */
    private Map<Long, Integer> initAssignedCount(final Collection<Long> userIds) {
        final Map<Long, Integer> assignedCount = new HashMap<>();
        for (final Long uid : userIds) assignedCount.put(uid, 0);
        return assignedCount;
    }

    /** 슬롯당 필요 면접관 수 */
    private int resolveNeedPerSlot(final Interview interview) {
        final Integer perSlot = interview.getInterviewerPerSlot();
        return (perSlot != null && perSlot > 0) ? perSlot : 1;
    }

    /**
     * 단일 슬롯 배정 처리
     */
    private void processSlot(final TimeSlot slot,
                             final int needPerSlot,
                             final CandidateIndex index,
                             final Map<Long, Integer> assignedCount,
                             final BusyCalendar busyCalendar) {

        final LocalDateTime slotStart = slot.getDate().atTime(slot.getStartTime());
        final LocalDateTime slotEnd   = slot.getDate().atTime(slot.getEndTime());
        final Position slotPosition   = slot.getPosition();

        log.info("[4] 타임슬롯 ID: {} / 시간: {} / 포지션: {}",
                slot.getId(), slotStart, slotPosition != null ? slotPosition.getName() : "null");

        // 시간/포지션 일치 후보
        final List<Long> prelim = candidateSelector.preliminaryCandidates(slotStart, slotPosition, index);

        // 동시간대 겹침 제거
        final List<Long> feasible = candidateSelector.feasibleCandidates(prelim, slotStart, slotEnd, index, busyCalendar);

        // 공평 분배(덜 배정된 사용자 먼저)
        final List<Long> sorted = feasible.stream()
                .sorted(fairnessPolicy.comparator(assignedCount))
                .toList();

        int assignedInThisSlot = 0;
        for (final Long uid : sorted) {
            if (assignedInThisSlot >= needPerSlot) break;

            final User user = index.getUserMap().get(uid);
            assignmentRecorder.assign(slot, user, InterviewRole.INTERVIEWER);
            busyCalendar.markBusy(uid, slotStart, slotEnd);
            assignedCount.merge(uid, 1, Integer::sum);

            log.info("[SUCCESS] 배정: userId={} ({})", uid, user.getName());
            assignedInThisSlot++;
        }

        if (assignedInThisSlot == 0) {
            log.warn("[WARN] 배정 가능한 운영진 없음 (timeSlotId={})", slot.getId());
        }
    }
}
