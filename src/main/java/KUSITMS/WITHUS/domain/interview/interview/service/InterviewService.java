package KUSITMS.WITHUS.domain.interview.interview.service;

import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewScheduleDTO;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.user.user.entity.User;

import java.util.List;

public interface InterviewService {
    Long create(Long recruitmentId);
    Interview getById(Long interviewId);
    List<InterviewScheduleDTO.MyInterviewScheduleSummaryDTO> getMyOrganizationInterviews(User user);
}
