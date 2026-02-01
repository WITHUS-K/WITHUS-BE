package KUSITMS.WITHUS.domain.interview.timeslot.service;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.interview.timeslot.dto.TimeSlotResponseDTO;

import java.util.List;

public interface TimeSlotService {
    List<ApplicationResponseDTO.DetailForTimeSlot> getApplicationsByTimeSlotFilteredByUser(Long timeSlotId, Long currentUserId);
    void addApplicantToTimeSlot(Long timeSlotId, List<Long> applicantIds);
    void updateApplicantInTimeSlot(Long timeSlotId, List<Long> applicantIds);
    TimeSlotResponseDTO.Detail getTimeSlotDetail(Long timeSlotId);
}
