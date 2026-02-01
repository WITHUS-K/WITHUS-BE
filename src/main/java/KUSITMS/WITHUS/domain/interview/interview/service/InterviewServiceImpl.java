package KUSITMS.WITHUS.domain.interview.interview.service;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewResponseDTO;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewScheduleDTO;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.interview.repository.InterviewRepository;
import KUSITMS.WITHUS.domain.interview.interview.service.assembler.InterviewScheduleAssembler;
import KUSITMS.WITHUS.domain.interview.timeslot.dto.TimeSlotResponseDTO;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslot.repository.TimeSlotRepository;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import KUSITMS.WITHUS.domain.interview.timeslotUser.repository.TimeSlotUserRepository;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final TimeSlotUserRepository timeSlotUserRepository;
    private final InterviewScheduleAssembler scheduleAssembler;

    @Override
    @Transactional
    public Long create(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.getById(recruitmentId);

        Interview interview = Interview.builder()
                .recruitment(recruitment)
                .build();

        List<Application> applications = applicationRepository.findPassedByRecruitment(recruitmentId);

        for (Application app : applications) {
            app.associateInterview(interview);
        }

        return interviewRepository.save(interview).getId();
    }

    @Override
    public Interview getById(Long interviewId) {
        return interviewRepository.getById(interviewId);
    }

    @Override
    public List<InterviewScheduleDTO.InterviewScheduleSummaryDTO> getOrganizationInterviews(Long organizationId) {
        // 해당 조직의 모든 공고 조회
        List<Recruitment> recruitments = recruitmentRepository.findAllByOrganizationId(organizationId);
        if (recruitments.isEmpty()) {
            return Collections.emptyList();
        }

        // 공고 ID 리스트 추출
        List<Long> recruitmentIds = recruitments.stream()
                .map(Recruitment::getId)
                .toList();

        // 해당 공고들에 매핑된 Interview 조회
        List<Interview> interviews = interviewRepository.findAllByRecruitmentIdIn(recruitmentIds);
        Map<Long, Interview> interviewByRecruitmentId = interviews.stream()
                .collect(Collectors.toMap(
                        i -> i.getRecruitment().getId(),
                        Function.identity()
                ));

        // DTO 변환
        return recruitments.stream()
                .filter(r -> interviewByRecruitmentId.containsKey(r.getId()))
                .map(r -> {
                    Interview interview = interviewByRecruitmentId.get(r.getId());

                    List<AvailableTimeRangeResponseDTO> timeRanges = r.getAvailableTimeRanges().stream()
                            .map(AvailableTimeRangeResponseDTO::from)
                            .toList();

                    return new InterviewScheduleDTO.InterviewScheduleSummaryDTO(
                            r.getId(),
                            r.getTitle(),
                            interview.getId(),
                            timeRanges,
                            r.getInterviewDuration()
                    );
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewResponseDTO.Schedule getMyInterviewSchedule(User user, InterviewRole role) {
        List<Long> timeSlotIds = timeSlotUserRepository.findMyTimeSlotIds(user.getId(), role);
        if (timeSlotIds.isEmpty()) {
            return InterviewResponseDTO.Schedule.from(role, List.of());
        }

        List<TimeSlot> slots = timeSlotRepository.findAllByIdIn(timeSlotIds);
        Map<Long, List<TimeSlotUser>> tsUsersBySlotId = scheduleAssembler.loadTimeSlotUsersGrouped(timeSlotIds);

        List<TimeSlotResponseDTO.ScheduleCard> cards =
                scheduleAssembler.buildScheduleCards(slots, tsUsersBySlotId);

        List<InterviewResponseDTO.Schedule.DateGroup> dateGroups =
                scheduleAssembler.groupCardsByDate(slots, cards);

        return new InterviewResponseDTO.Schedule(role, dateGroups);
    }

    @Override
    public InterviewResponseDTO.Config getInterviewConfig(Long interviewId) {
        Interview interview = interviewRepository.getById(interviewId);

        return InterviewResponseDTO.Config.from(
                interview,
                interview.getInterviewerPerSlot(),
                interview.getApplicantPerSlot(),
                interview.getAssistantPerSlot()
        );
    }

}
