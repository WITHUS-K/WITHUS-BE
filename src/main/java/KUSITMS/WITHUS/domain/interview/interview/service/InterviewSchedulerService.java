package KUSITMS.WITHUS.domain.interview.interview.service;

import KUSITMS.WITHUS.domain.application.applicantAvailability.entity.ApplicantAvailability;
import KUSITMS.WITHUS.domain.application.applicantAvailability.repository.ApplicantAvailabilityRepository;
import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewScheduleDTO;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.interview.repository.InterviewRepository;
import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.repository.InterviewerAvailabilityRepository;
import KUSITMS.WITHUS.domain.interview.timeslot.dto.SimSlot;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslot.repository.TimeSlotRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewSchedulerService {

    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ApplicantAvailabilityRepository availabilityRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final InterviewerAvailabilityRepository interviewerAvailabilityRepository;

    /**
     * 면접 타임슬롯 배정
     * @param recruitmentId 공고 ID
     * @param interviewId 면접 ID
     * @param config 배정할 때 필요한 설정
     */
    @Transactional
    public void assignInterviewSlots(Long recruitmentId, Long interviewId, InterviewConfig config) {
        if (config.roomNames().size() != config.roomCount()) {
            throw new CustomException(ErrorCode.MISMATCHED_ROOM_COUNT);
        }

        // 1. 서류 합격 지원자 및 가능 시간 조회
        List<Application> applicants = new ArrayList<>(
                applicationRepository.findPassedByRecruitment(recruitmentId));
        List<ApplicantAvailability> availabilityList =
                availabilityRepository.findByApplicationIn(applicants);
        Interview interview = interviewRepository.getById(interviewId);

        // 기존 슬롯 초기화
        List<TimeSlot> oldSlots = timeSlotRepository.findByInterviewId(interview.getId());
        oldSlots.forEach(slot -> slot.getApplications().forEach(app -> app.assignTimeSlot(null)));
        timeSlotRepository.deleteAll(oldSlots);

        interview.setConfig(
                config.interviewerPerSlot,
                config.applicantPerSlot,
                config.assistantPerSlot,
                config.roomCount()
        );
        interview.setRoomNames(config.roomNames());

        // 2. 맵 구성
        Map<Long, Application> applicantMap = applicants.stream()
                .collect(Collectors.toMap(Application::getId, Function.identity()));
        Map<Long, List<LocalDateTime>> availabilityMap = availabilityList.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getApplication().getId(),
                        Collectors.mapping(ApplicantAvailability::getAvailableTime, Collectors.toList())
                ));

        // 3. 각 시간대별로 포지션에 따라 타임슬롯을 나누고, 해당 시간에 생성된 슬롯 개수도 함께 관리
        Map<LocalDateTime, List<SimSlot>> slotPool = new HashMap<>();

        // 4. 가능한 시간 적은 지원자 우선 배정
        List<Long> applicantIds = new ArrayList<>(availabilityMap.keySet());
        applicantIds.sort(Comparator.comparingInt(id -> availabilityMap.get(id).size()));

        // 5. 백트래킹으로 전체 배정 시도
        int slotMinutes = recruitmentRepository.getById(recruitmentId).getInterviewDuration();
        Map<Long, SimSlot> finalAssignment = new HashMap<>();
        boolean success = backtrackAssign(
                0,
                applicantIds,
                availabilityMap,
                applicantMap,
                slotPool,
                config,
                finalAssignment,
                slotMinutes
        );

        if (!success) {
            System.out.println("전원 배정 실패");
        }

        // 6. 배정 결과 저장
        finalAssignment.forEach((aid, sim) -> {
            TimeSlot slot = timeSlotRepository.findOrCreate(
                    sim.date(), sim.start(), sim.end(),
                    interview, sim.organizationRole(), sim.roomName()
            );
            applicantMap.get(aid).assignTimeSlot(slot);
            System.out.printf(
                    "배정 완료 - 지원자 %d / 방 %s / 시간 %s %s%n",
                    aid, slot.getRoomName(), slot.getDate(), slot.getStartTime()
            );
        });
        System.out.printf("최종 배정: %d/%d%n", finalAssignment.size(), applicants.size());

    }

    /**
     * 백트래킹을 통해 모든 지원자 배정을 시도
     */
    private boolean backtrackAssign(
            int index,
            List<Long> applicantIds,
            Map<Long, List<LocalDateTime>> availabilityMap,
            Map<Long, Application> applicantMap,
            Map<LocalDateTime, List<SimSlot>> slotPool,
            InterviewConfig config,
            Map<Long, SimSlot> finalAssignment,
            int slotMinutes
    ) {
        if (index == applicantIds.size()) return true;

        Long applicantId = applicantIds.get(index);
        Application applicant = applicantMap.get(applicantId);

        // recruitment의 역할 존재 여부 확인
        Recruitment recruitment = applicant.getRecruitment();
        boolean recruitmentHasOrganizationRole = recruitment.getPositions() != null && !recruitment.getPositions().isEmpty();

        Long organizationRoleId = applicant.getOrganizationRole() != null ? applicant.getOrganizationRole().getId() : 0L;

        for (LocalDateTime time : availabilityMap.getOrDefault(applicantId, List.of())) {
            List<SimSlot> slots = slotPool.computeIfAbsent(time, t -> new ArrayList<>());

            // 기존 슬롯 중 정원이 남은 슬롯이 있는지 확인
            for (SimSlot s : slots) {
                boolean sameOrganizationRole;
                if (!recruitmentHasOrganizationRole) {
                    sameOrganizationRole = true;
                } else {
                    sameOrganizationRole = (s.organizationRole() == null && organizationRoleId == 0L)
                            || (s.organizationRole() != null && Objects.equals(s.organizationRole().getId(), organizationRoleId));
                }
                if (sameOrganizationRole) {
                    long used = finalAssignment.values().stream()
                            .filter(x -> x.equals(s)).count();
                    if (used < config.applicantPerSlot) {
                        finalAssignment.put(applicantId, s);
                        if (backtrackAssign(index + 1, applicantIds, availabilityMap,
                                applicantMap, slotPool, config, finalAssignment, slotMinutes)) {
                            return true;
                        }
                        finalAssignment.remove(applicantId);
                    }
                }
            }

            if (slots.size() < config.roomCount()) {
                LocalDate date = time.toLocalDate();
                LocalTime st = time.toLocalTime();
                LocalTime en = st.plusMinutes(slotMinutes);
                String room = config.roomNames().get(slots.size());
                SimSlot newSlot = new SimSlot(date, st, en, recruitmentHasOrganizationRole ? applicant.getOrganizationRole() : null, room);

                slots.add(newSlot);
                finalAssignment.put(applicantId, newSlot);
                if (backtrackAssign(index + 1, applicantIds, availabilityMap,
                        applicantMap, slotPool, config, finalAssignment, slotMinutes)) {
                    return true;
                }
                finalAssignment.remove(applicantId);
                slots.remove(newSlot);
            }
        }
        return false;
    }

    /**
     * 특정 면접의 배정된 전체 타임슬롯 조회
     * @param interviewId 조회할 면접 ID
     * @return 조회한 타임슬롯의 정보
     */
    public List<InterviewScheduleDTO> getInterviewSchedule(Long interviewId) {
        Interview interview = interviewRepository.getById(interviewId);
        Recruitment recruitment = interview.getRecruitment();

        List<TimeSlot> slots = timeSlotRepository.findByInterviewId(interview.getId());
        return buildScheduleDTOs(interview, false, recruitment, slots, false);
    }

    /**
     * 내 면접 시간 조회 (배정된 타임 슬롯 조회)
     * @param interviewId 조회할 면접 ID
     * @param user 현재 로그인 유저
     * @return 조회된 타임 슬롯 반환
     */
    public List<InterviewScheduleDTO> getMyInterviewTimes(Long interviewId, User user) {
        Interview interview = interviewRepository.getById(interviewId);
        Recruitment recruitment = interview.getRecruitment();

        Set<Long> mySlotIds = timeSlotRepository.findAllByUserInvolved(interviewId, user).stream()
                .map(TimeSlot::getId)
                .collect(Collectors.toSet());

        List<TimeSlot> mySlots = timeSlotRepository.findByInterviewId(interview.getId()).stream()
                .filter(slot -> mySlotIds.contains(slot.getId()))
                .toList();

        // 면접 가능 시간 제출 여부 확인
        boolean hasSubmittedAvailability = interviewerAvailabilityRepository.existsByInterviewAndUser(interview.getId(), user.getId());

        return buildScheduleDTOs(interview, hasSubmittedAvailability, recruitment, mySlots, true);
    }

    /**
     * 타임테이블 초기화
     * @param interviewId 초기화 할 면접 ID
     */
    @Transactional
    public void resetInterviewSchedule(Long interviewId) {
        Interview interview = interviewRepository.getById(interviewId);

        // application 삭제되지 않도록 timeSlot을 null로 초기화
        List<TimeSlot> timeSlots = timeSlotRepository.findByInterviewId(interview.getId());
        for (TimeSlot timeSlot : timeSlots) {
            for (Application application : timeSlot.getApplications()) {
                application.assignTimeSlot(null);
            }
        }

        timeSlotRepository.deleteAllByInterview(interview.getId());

        interview.setConfig(0, 0, 0, 0);
        interview.setRoomNames(List.of());

        System.out.println("면접 타임테이블 초기화 완료");
    }


    private List<InterviewScheduleDTO> buildScheduleDTOs(Interview interview, boolean hasSubmittedAvailability, Recruitment recruitment,
                                                         List<TimeSlot> slots, boolean isMySchedule) {
        Map<LocalDate, List<TimeSlot>> slotsByDate = slots.stream()
                .collect(Collectors.groupingBy(TimeSlot::getDate));

        return recruitment.getAvailableTimeRanges().stream()
                .map(timeRange -> {
                    LocalDate date = timeRange.getDate();
                    List<TimeSlot> daySlots = slotsByDate.getOrDefault(date, List.of());

                    List<InterviewScheduleDTO.InterviewSlotDTO> slotDTOs = daySlots.stream()
                            .map(slot -> isMySchedule
                                    ? InterviewScheduleDTO.InterviewSlotDTO.from(slot, true)
                                    : InterviewScheduleDTO.InterviewSlotDTO.from(slot))
                            .toList();

                    List<String> roomNames = interview.getRoomNames();

                    return InterviewScheduleDTO.from(
                            interview.getId(),
                            hasSubmittedAvailability,
                            date,
                            timeRange.getStartTime(),
                            timeRange.getEndTime(),
                            recruitment.getInterviewDuration(),
                            roomNames,
                            slotDTOs
                    );
                })
                .sorted(Comparator.comparing(InterviewScheduleDTO::date))
                .toList();
    }


    /**
     * 면접 구성 설정
     */
    public record InterviewConfig(
            int interviewerPerSlot,
            int applicantPerSlot,
            int assistantPerSlot,
            int roomCount,
            List<String> roomNames
    ) {}
}
