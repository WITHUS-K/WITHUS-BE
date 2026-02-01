package KUSITMS.WITHUS.domain.interview.interview.service;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewResponseDTO;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewScheduleDTO;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.interview.repository.InterviewRepository;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslot.repository.TimeSlotRepository;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import KUSITMS.WITHUS.domain.interview.timeslotUser.repository.TimeSlotUserRepository;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
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
    public InterviewResponseDTO.MyInterviewSchedule getMyInterviewSchedule(User user, InterviewRole role) {

        List<Long> timeSlotIds = timeSlotUserRepository.findMyTimeSlotIds(user.getId(), role);
        if (timeSlotIds.isEmpty()) {
            return new InterviewResponseDTO.MyInterviewSchedule(role, List.of());
        }

        // 1) 슬롯 + (interview/recruitment) + applications 로딩
        List<TimeSlot> slots = timeSlotRepository.findAllByIdIn(timeSlotIds);

        // 2) 슬롯에 연결된 TimeSlotUser(+User) 로딩
        List<TimeSlotUser> tsUsers = timeSlotUserRepository.findAllByTimeSlotIdInWithUser(timeSlotIds);

        Map<Long, List<TimeSlotUser>> tsUsersBySlotId = tsUsers.stream()
                .collect(Collectors.groupingBy(tsu -> tsu.getTimeSlot().getId()));

        // 3) DTO로 변환
        List<InterviewResponseDTO.MyInterviewSchedule.SlotCard> cards = slots.stream()
                .map(slot -> {
                    List<InterviewResponseDTO.MyInterviewSchedule.ApplicantSimple> applicants =
                            slot.getApplications().stream()
                                    .map(app -> new InterviewResponseDTO.MyInterviewSchedule.ApplicantSimple(app.getId(), app.getName()))
                                    .toList();

                    List<TimeSlotUser> usersInSlot = tsUsersBySlotId.getOrDefault(slot.getId(), List.of());

                    List<InterviewResponseDTO.MyInterviewSchedule.UserSimple> interviewers =
                            usersInSlot.stream()
                                    .filter(u -> u.getRole() == InterviewRole.INTERVIEWER)
                                    .map(u -> new InterviewResponseDTO.MyInterviewSchedule.UserSimple(
                                            u.getUser().getId(),
                                            u.getUser().getName(),
                                            u.getUser().getProfileImageUrl()
                                    ))
                                    .toList();

                    List<InterviewResponseDTO.MyInterviewSchedule.UserSimple> assistants =
                            usersInSlot.stream()
                                    .filter(u -> u.getRole() == InterviewRole.ASSISTANT)
                                    .map(u -> new InterviewResponseDTO.MyInterviewSchedule.UserSimple(
                                            u.getUser().getId(),
                                            u.getUser().getName(),
                                            u.getUser().getProfileImageUrl()
                                    ))
                                    .toList();

                    return new InterviewResponseDTO.MyInterviewSchedule.SlotCard(
                            slot.getId(),
                            slot.getInterview().getId(),
                            slot.getRoomName(),
                            slot.getStartTime(),
                            slot.getEndTime(),
                            applicants,
                            interviewers,
                            assistants
                    );
                })
                // 시간순 정렬(같은 날짜 안에서)
                .sorted(Comparator
                        .comparing(InterviewResponseDTO.MyInterviewSchedule.SlotCard::startTime)
                        .thenComparing(InterviewResponseDTO.MyInterviewSchedule.SlotCard::timeSlotId))
                .toList();

        // 4) 날짜별 그룹핑
        Map<LocalDate, List<InterviewResponseDTO.MyInterviewSchedule.SlotCard>> byDate = new LinkedHashMap<>();
        for (TimeSlot slot : slots.stream()
                .sorted(Comparator.comparing(TimeSlot::getDate).thenComparing(TimeSlot::getStartTime))
                .toList()) {
            byDate.putIfAbsent(slot.getDate(), new ArrayList<>());
        }

        // cards는 slotId 기반이라 date 매핑이 필요함
        Map<Long, LocalDate> slotDateMap = slots.stream()
                .collect(Collectors.toMap(TimeSlot::getId, TimeSlot::getDate));

        for (InterviewResponseDTO.MyInterviewSchedule.SlotCard card : cards) {
            LocalDate d = slotDateMap.get(card.timeSlotId());
            byDate.computeIfAbsent(d, k -> new ArrayList<>()).add(card);
        }

        List<InterviewResponseDTO.MyInterviewSchedule.DateGroup> dateGroups = byDate.entrySet().stream()
                .map(e -> new InterviewResponseDTO.MyInterviewSchedule.DateGroup(e.getKey(), e.getValue()))
                .toList();

        return new InterviewResponseDTO.MyInterviewSchedule(role, dateGroups);
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
