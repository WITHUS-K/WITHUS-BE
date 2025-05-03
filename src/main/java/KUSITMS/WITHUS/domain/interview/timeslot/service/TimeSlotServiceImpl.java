package KUSITMS.WITHUS.domain.interview.timeslot.service;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.evaluation.evaluation.repository.EvaluationRepository;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslot.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final EvaluationRepository evaluationRepository;

    @Override
    public List<ApplicationResponseDTO.Detail> getApplicationsByTimeSlot(Long timeSlotId) {
        TimeSlot timeSlot = timeSlotRepository.getById(timeSlotId);

        return timeSlot.getApplications().stream()
                .map(application -> {
                    List<Evaluation> evaluations = evaluationRepository.findEvaluationsForApplication(application.getId());
                    return ApplicationResponseDTO.Detail.from(application, List.of(), evaluations);
                })
                .toList();
    }
}
