package KUSITMS.WITHUS.domain.interview.timeslot.service;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.application.comment.entity.Comment;
import KUSITMS.WITHUS.domain.application.comment.enumerate.CommentType;
import KUSITMS.WITHUS.domain.application.comment.repository.CommentRepository;
import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.evaluation.evaluation.repository.EvaluationRepository;
import KUSITMS.WITHUS.domain.interview.timeslot.dto.Applicant;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslot.repository.TimeSlotRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final EvaluationRepository evaluationRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;


    @Override
    public List<ApplicationResponseDTO.DetailForTimeSlot> getApplicationsByTimeSlotFilteredByUser(Long timeSlotId, Long currentUserId) {
        TimeSlot timeSlot = timeSlotRepository.getById(timeSlotId);

        User user = userRepository.getById(currentUserId);
        boolean isAdmin = user.getRole() == Role.ADMIN;

        return timeSlot.getApplications().stream()
                .map(application -> {
                    List<Evaluation> evaluations = evaluationRepository.findEvaluationsForApplication(application.getId());

                    List<Comment> documentComments = isAdmin
                            ? commentRepository.findByApplicationIdAndType(application.getId(), CommentType.DOCUMENT)
                            : commentRepository.findByApplicationIdAndTypeAndUser(application.getId(), CommentType.DOCUMENT, currentUserId);

                    List<Comment> interviewComments = isAdmin
                            ? commentRepository.findByApplicationIdAndType(application.getId(), CommentType.INTERVIEW)
                            : commentRepository.findByApplicationIdAndTypeAndUser(application.getId(), CommentType.INTERVIEW, currentUserId);

                    return ApplicationResponseDTO.DetailForTimeSlot.from(application, timeSlot, evaluations, documentComments, interviewComments);
                })
                .toList();
    }

    @Override
    @Transactional
    public void addApplicantToTimeSlot(Long timeSlotId, List<Long> applicantIds) {
        TimeSlot timeSlot = timeSlotRepository.getById(timeSlotId);
        List<Application> applicants = applicationRepository.findAllById(applicantIds);

        for (Application applicant : applicants) {
            timeSlot.addApplication(applicant);
        }

        timeSlotRepository.save(timeSlot);
    }

    @Override
    @Transactional
    public void updateApplicantInTimeSlot(Long timeSlotId, List<Long> applicantIds) {
        TimeSlot timeSlot = timeSlotRepository.getById(timeSlotId);

        // 현재 배정된 지원자 정보
        List<Applicant> existingApplications = timeSlot.getApplications().stream()
                .map(application -> new Applicant(application.getId(), application))
                .toList();

        // 삭제 대상 처리
        timeSlot.getApplications().removeIf(application -> {
            boolean shouldRemove = !applicantIds.contains(application.getId());
            if (shouldRemove) {
                application.assignTimeSlot(null);
            }
            return shouldRemove;
        });

        // 추가 대상 처리
        List<Long> toAdd = applicantIds.stream()
                .filter(id -> existingApplications.stream().noneMatch(record -> record.applicationId().equals(id)))
                .collect(Collectors.toList());

        // Time Slot에 추가
        List<Application> applicationsToAdd = applicationRepository.findAllById(toAdd);
        for (Application application : applicationsToAdd) {
            timeSlot.addApplication(application);
        }

        timeSlotRepository.save(timeSlot);
    }

}
