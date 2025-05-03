package KUSITMS.WITHUS.domain.interview.timeslot.service;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.comment.entity.Comment;
import KUSITMS.WITHUS.domain.application.comment.enumerate.CommentType;
import KUSITMS.WITHUS.domain.application.comment.repository.CommentRepository;
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
    private final CommentRepository commentRepository;


    @Override
    public List<ApplicationResponseDTO.DetailForTimeSlot> getApplicationsByTimeSlotFilteredByUser(Long timeSlotId, Long currentUserId) {
        TimeSlot timeSlot = timeSlotRepository.getById(timeSlotId);

        return timeSlot.getApplications().stream()
                .map(application -> {
                    List<Evaluation> evaluations = evaluationRepository.findEvaluationsForApplication(application.getId());

                    List<Comment> documentComments = commentRepository.findByApplicationIdAndTypeAndCreatedBy(
                            application.getId(), CommentType.DOCUMENT, currentUserId);
                    List<Comment> interviewComments = commentRepository.findByApplicationIdAndTypeAndCreatedBy(
                            application.getId(), CommentType.INTERVIEW, currentUserId);

                    return ApplicationResponseDTO.DetailForTimeSlot.from(application, evaluations, documentComments, interviewComments);
                })
                .toList();
    }
}
