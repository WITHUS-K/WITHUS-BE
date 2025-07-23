package KUSITMS.WITHUS.domain.interview.timeslotUser.service;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.interview.repository.InterviewRepository;
import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity.InterviewerAvailability;
import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.repository.InterviewerAvailabilityRepository;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslot.repository.TimeSlotRepository;
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

    @Override
    @Transactional
    public void assignInterviewers(Long interviewId) {
        // 기존 면접관 배정 삭제
        timeSlotUserRepository.deleteByInterviewIdAndRole(interviewId, InterviewRole.INTERVIEWER);

        Interview interview = interviewRepository.getById(interviewId);
        List<TimeSlot> timeSlots = timeSlotRepository.findByInterviewId(interviewId);
        List<InterviewerAvailability> availabilities = interviewerAvailabilityRepository.findByInterviewId(interviewId);

        log.info("[1] 타임슬롯 수: {}", timeSlots.size());
        log.info("[2] 인터뷰어 가능 시간 제출 수: {}", availabilities.size());

        Map<Long, List<LocalDateTime>> userAvailableTimes = availabilities.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getUser().getId(),
                        Collectors.mapping(InterviewerAvailability::getAvailableTime, Collectors.toList())
                ));

        log.info("[3] 가능한 유저 수: {}", userAvailableTimes.size());

        Map<Long, User> userMap = userRepository.findAllById(new ArrayList<>(userAvailableTimes.keySet()))
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        Set<Long> usedUsers = new HashSet<>();

        for (TimeSlot slot : timeSlots) {
            LocalDateTime slotTime = slot.getDate().atTime(slot.getStartTime());
            Position slotPosition = slot.getPosition();

            log.info("[4] 타임슬롯 ID: {} / 시간: {} / 포지션: {}",
                    slot.getId(), slotTime, slotPosition != null ? slotPosition.getName() : "null");

            List<Long> assignableUserIds = userAvailableTimes.entrySet().stream()
                    .filter(e -> {
                        boolean hasTime = e.getValue().contains(slotTime);
                        if (!hasTime) {
                            log.debug("[FAIL] userId={} 는 시간 불일치", e.getKey());
                        }
                        return hasTime;
                    })
                    .map(Map.Entry::getKey)
                    .filter(userId -> {
                        User user = userMap.get(userId);
                        boolean alreadyUsed = usedUsers.contains(userId);
                        boolean matchRole = slotPosition == null || user.hasMatchingRole(slotPosition);

                        if (alreadyUsed) {
                            log.debug("[FAIL] userId={} ({}) 이미 배정됨", userId, user.getName());
                            return false;
                        }
                        if (!matchRole) {
                            log.debug("[FAIL] userId={} ({}) 포지션 불일치", userId, user.getName());
                            log.debug("유저 역할 목록: {}", user.getUserOrganizationRoles().stream()
                                    .map(r -> r.getOrganizationRole().getName())
                                    .toList());
                        }

                        return matchRole;
                    })
                    .limit(interview.getInterviewerPerSlot())
                    .toList();

            for (Long userId : assignableUserIds) {
                User user = userMap.get(userId);
                assignToSlot(slot, user, InterviewRole.INTERVIEWER);
                usedUsers.add(userId);
                log.info("[SUCCESS] 배정: userId={} ({})", userId, user.getName());
            }

            if (assignableUserIds.isEmpty()) {
                log.warn("[WARN] 배정 가능한 운영진 없음 (timeSlotId={})", slot.getId());
            }
        }
    }
    
    private void assignToSlot(TimeSlot slot, User user, InterviewRole role) {
        TimeSlotUser tsu = TimeSlotUser.builder()
                .timeSlot(slot)
                .user(user)
                .role(role)
                .build();
        timeSlotUserRepository.save(tsu);
        slot.addTimeSlotUser(tsu);
    }
}
