package KUSITMS.WITHUS.domain.interview.interview.service;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewResponseDTO;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewScheduleDTO;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.user.user.entity.User;

import java.util.List;

public interface InterviewService {
    Long create(Long recruitmentId);
    Interview getById(Long interviewId);
    InterviewResponseDTO.Config getInterviewConfig(Long interviewId);
    List<InterviewScheduleDTO.InterviewScheduleSummaryDTO> getOrganizationInterviews(Long organizationId);
    InterviewResponseDTO.Schedule getMyInterviewSchedule(User user, InterviewRole role);
}
