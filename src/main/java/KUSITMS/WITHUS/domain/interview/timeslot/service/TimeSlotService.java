package KUSITMS.WITHUS.domain.interview.timeslot.service;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;

import java.util.List;

public interface TimeSlotService {
    List<ApplicationResponseDTO.DetailForTimeSlot> getApplicationsByTimeSlotFilteredByUser(Long timeSlotId, Long currentUserId);
}
