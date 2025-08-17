package KUSITMS.WITHUS.domain.interview.timeslot.service.util;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import KUSITMS.WITHUS.domain.interview.timeslotUser.repository.TimeSlotUserRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignmentRecorder {

    private final TimeSlotUserRepository timeSlotUserRepository;

    /**
     * TimeSlot에 사용자 배정
     */
    public void assign(final TimeSlot slot, final User user, final InterviewRole role) {
        final TimeSlotUser timeSlotUser = TimeSlotUser.builder()
                .timeSlot(slot)
                .user(user)
                .role(role)
                .build();

        slot.addTimeSlotUser(timeSlotUser);
        timeSlotUserRepository.save(timeSlotUser);
    }
}
