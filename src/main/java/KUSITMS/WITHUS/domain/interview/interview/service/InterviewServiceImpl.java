package KUSITMS.WITHUS.domain.interview.interview.service;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewResponseDTO;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewScheduleDTO;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.interview.repository.InterviewRepository;
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

        // 타임슬롯 + (interview/recruitment) + applications 로딩
        List<TimeSlot> slots = timeSlotRepository.findAllByIdIn(timeSlotIds);

        // 타임슬롯에 연결된 TimeSlotUser(+User) 로딩
        List<TimeSlotUser> tsUsers = timeSlotUserRepository.findAllByTimeSlotIdInWithUser(timeSlotIds);

        Map<Long, List<TimeSlotUser>> tsUsersBySlotId = tsUsers.stream()
                .collect(Collectors.groupingBy(tsu -> tsu.getTimeSlot().getId()));

        // DTO로 변환
        List<TimeSlotResponseDTO.ScheduleCard> cards = slots.stream()
                .map(slot -> {
                    List<ApplicationResponseDTO.Applicant> applicants =
                            slot.getApplications().stream()
                                    .map(ApplicationResponseDTO.Applicant::from)
                                    .toList();

                    List<TimeSlotUser> usersInSlot = tsUsersBySlotId.getOrDefault(slot.getId(), List.of());

                    List<UserResponseDTO.Summary> interviewers =
                            usersInSlot.stream()
                                    .filter(u -> u.getRole() == InterviewRole.INTERVIEWER)
                                    .map(u -> UserResponseDTO.Summary.from(u.getUser()))
                                    .toList();

                    List<UserResponseDTO.Summary> assistants =
                            usersInSlot.stream()
                                    .filter(u -> u.getRole() == InterviewRole.ASSISTANT)
                                    .map(u -> UserResponseDTO.Summary.from(u.getUser()))
                                    .toList();

                    return TimeSlotResponseDTO.ScheduleCard.from(slot, applicants, interviewers, assistants);
                })
                // 시간순 정렬(같은 날짜 안에서)
                .sorted(Comparator
                        .comparing(TimeSlotResponseDTO.ScheduleCard::startTime)
                        .thenComparing(TimeSlotResponseDTO.ScheduleCard::timeSlotId))
                .toList();

        // 날짜별 그룹핑
        Map<LocalDate, List<TimeSlotResponseDTO.ScheduleCard>> byDate = new LinkedHashMap<>();
        for (TimeSlot slot : slots.stream()
                .sorted(Comparator.comparing(TimeSlot::getDate).thenComparing(TimeSlot::getStartTime))
                .toList()) {
            byDate.putIfAbsent(slot.getDate(), new ArrayList<>());
        }

        // cards는 slotId 기반이라 date 매핑이 필요함
        Map<Long, LocalDate> slotDateMap = slots.stream()
                .collect(Collectors.toMap(TimeSlot::getId, TimeSlot::getDate));

        for (TimeSlotResponseDTO.ScheduleCard card : cards) {
            LocalDate d = slotDateMap.get(card.timeSlotId());
            byDate.computeIfAbsent(d, k -> new ArrayList<>()).add(card);
        }

        List<InterviewResponseDTO.Schedule.DateGroup> dateGroups = byDate.entrySet().stream()
                .map(e -> InterviewResponseDTO.Schedule.DateGroup.from(e.getKey(), e.getValue()))
                .toList();

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
