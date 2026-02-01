package KUSITMS.WITHUS.domain.interview.interview.service.assembler;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewResponseDTO;
import KUSITMS.WITHUS.domain.interview.timeslot.dto.TimeSlotResponseDTO;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import KUSITMS.WITHUS.domain.interview.timeslotUser.repository.TimeSlotUserRepository;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InterviewScheduleAssembler {

    private final TimeSlotUserRepository timeSlotUserRepository;

    public Map<Long, List<TimeSlotUser>> loadTimeSlotUsersGrouped(List<Long> timeSlotIds) {
        return timeSlotUserRepository.findAllByTimeSlotIdInWithUser(timeSlotIds)
                .stream()
                .collect(Collectors.groupingBy(tsu -> tsu.getTimeSlot().getId()));
    }

    public List<TimeSlotResponseDTO.ScheduleCard> buildScheduleCards(
            List<TimeSlot> slots,
            Map<Long, List<TimeSlotUser>> tsUsersBySlotId
    ) {
        return slots.stream()
                .map(slot -> toScheduleCard(slot, tsUsersBySlotId))
                .sorted(Comparator
                        .comparing(TimeSlotResponseDTO.ScheduleCard::startTime)
                        .thenComparing(TimeSlotResponseDTO.ScheduleCard::timeSlotId))
                .toList();
    }

    public List<InterviewResponseDTO.Schedule.DateGroup> groupCardsByDate(
            List<TimeSlot> slots,
            List<TimeSlotResponseDTO.ScheduleCard> cards
    ) {
        Map<Long, LocalDate> slotDateMap = slots.stream()
                .collect(Collectors.toMap(TimeSlot::getId, TimeSlot::getDate));

        Map<LocalDate, List<TimeSlotResponseDTO.ScheduleCard>> byDate = new LinkedHashMap<>();

        slots.stream()
                .sorted(Comparator.comparing(TimeSlot::getDate)
                        .thenComparing(TimeSlot::getStartTime))
                .forEach(slot -> byDate.putIfAbsent(slot.getDate(), new ArrayList<>()));

        for (TimeSlotResponseDTO.ScheduleCard card : cards) {
            LocalDate date = slotDateMap.get(card.timeSlotId());
            byDate.get(date).add(card);
        }

        return byDate.entrySet().stream()
                .map(e -> InterviewResponseDTO.Schedule.DateGroup.from(e.getKey(), e.getValue()))
                .toList();
    }

    private TimeSlotResponseDTO.ScheduleCard toScheduleCard(
            TimeSlot slot,
            Map<Long, List<TimeSlotUser>> tsUsersBySlotId
    ) {
        List<ApplicationResponseDTO.Applicant> applicants =
                slot.getApplications().stream()
                        .map(ApplicationResponseDTO.Applicant::from)
                        .toList();

        List<TimeSlotUser> usersInSlot = tsUsersBySlotId.getOrDefault(slot.getId(), List.of());

        List<UserResponseDTO.Summary> interviewers = filterUsersByRole(usersInSlot, InterviewRole.INTERVIEWER);
        List<UserResponseDTO.Summary> assistants = filterUsersByRole(usersInSlot, InterviewRole.ASSISTANT);

        return TimeSlotResponseDTO.ScheduleCard.from(slot, applicants, interviewers, assistants);
    }

    private List<UserResponseDTO.Summary> filterUsersByRole(List<TimeSlotUser> users, InterviewRole role) {
        return users.stream()
                .filter(u -> u.getRole() == role)
                .map(u -> UserResponseDTO.Summary.from(u.getUser()))
                .toList();
    }
}
