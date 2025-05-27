package KUSITMS.WITHUS.domain.interview.timeslotUser.service;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslot.repository.TimeSlotRepository;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import KUSITMS.WITHUS.domain.interview.timeslotUser.repository.TimeSlotUserRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TimeSlotUserServiceImpl implements TimeSlotUserService {

    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final TimeSlotUserRepository timeSlotUserRepository;

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
}
