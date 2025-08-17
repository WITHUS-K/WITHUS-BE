package KUSITMS.WITHUS.domain.interview.timeslot.service.util.selection.ordering;

import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslot.service.util.candidate.CandidateIndex;

import java.util.List;

public interface SlotOrderingStrategy {
    List<TimeSlot> order(List<TimeSlot> timeSlots, CandidateIndex index);
}
