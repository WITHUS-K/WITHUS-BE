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

import java.util.List;

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
}
