package KUSITMS.WITHUS.domain.interview.interview.service;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.application.availability.entity.ApplicantAvailability;
import KUSITMS.WITHUS.domain.application.availability.repository.ApplicantAvailabilityRepository;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.entity.AvailableTimeRange;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewScheduleDTO;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.interview.repository.InterviewRepository;
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

    /**
     * 면접 타임슬롯 배정
     * @param recruitmentId 공고 ID
     * @param interviewId 면접 ID
     * @param config 배정할 때 필요한 설정
     */
    @Transactional
    public void assignInterviewSlots(Long recruitmentId, Long interviewId, InterviewConfig config) {
        // 1. 서류 합격 지원자 및 가능 시간 조회
        List<Application> applicants = new ArrayList<>(applicationRepository.findPassedByRecruitment(recruitmentId));
        List<ApplicantAvailability> availabilityList = availabilityRepository.findByApplicationIn(applicants);
        Interview interview = interviewRepository.getById(interviewId);

        // 2. ID로 빠르게 찾기 위한 맵 구성
        Map<Long, Application> applicantMap = applicants.stream()
                .collect(Collectors.toMap(Application::getId, Function.identity()));
        Map<Long, List<LocalDateTime>> availabilityMap = availabilityList.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getApplication().getId(),
                        Collectors.mapping(ApplicantAvailability::getAvailableTime, Collectors.toList())
                ));

        // 포지션이 있는 경우와 없는 경우 구분
        boolean hasPosition = applicants.stream().anyMatch(app -> app.getPosition() != null);

        // 3. 각 시간대별로 포지션에 따라 타임슬롯을 나누고, 해당 시간에 생성된 슬롯 개수도 함께 관리
        Map<LocalDateTime, Map<Long, List<TimeSlot>>> slotPool = new HashMap<>();
        Map<LocalDateTime, Integer> slotsUsedPerTime = new HashMap<>();
        for (ApplicantAvailability avail : availabilityList) {
            slotPool.putIfAbsent(avail.getAvailableTime(), new HashMap<>());
        }

        // 4. 가능한 시간 적은 지원자 우선 배정
        List<Long> applicantIds = new ArrayList<>(availabilityMap.keySet());
        applicantIds.sort(Comparator.comparingInt(id -> availabilityMap.get(id).size()));

        // 5. 백트래킹으로 전체 배정 시도
        Recruitment recruitment = recruitmentRepository.getById(recruitmentId);
        int slotMinutes = recruitment.getInterviewDuration();

        Map<Long, TimeSlot> finalAssignment = new HashMap<>();
        boolean success = backtrackAssign(0, applicantIds, availabilityMap, applicantMap, slotPool, slotsUsedPerTime, config, interview, finalAssignment, hasPosition, slotMinutes);

        // 6. 배정 결과 저장
        if (success) {
            finalAssignment.forEach((applicantId, slot) -> {
                Application app = applicantMap.get(applicantId);
                app.assignTimeSlot(slot);
                Long positionId = app.getPosition() != null ? app.getPosition().getId() : null;
                System.out.println("배정 완료 - 지원자 ID: " + applicantId + " / 포지션: " + positionId + " / 시간: " + slot.getDate() + " " + slot.getStartTime());
            });
        } else {
            System.out.println("전원 배정 실패");
        }

        System.out.println("최종 배정 인원: " + finalAssignment.size() + " / 전체: " + applicants.size());
    }

    /**
     * 백트래킹을 통해 모든 지원자 배정을 시도
     */
    private boolean backtrackAssign(
            int index,
            List<Long> applicantIds,
            Map<Long, List<LocalDateTime>> availabilityMap,
            Map<Long, Application> applicantMap,
            Map<LocalDateTime, Map<Long, List<TimeSlot>>> slotPool, // ✅ 수정: TimeSlot → List<TimeSlot>
            Map<LocalDateTime, Integer> slotsUsedPerTime,
            InterviewConfig config,
            Interview interview,
            Map<Long, TimeSlot> finalAssignment,
            boolean hasPosition,
            int slotMinutes
    ) {
        if (index == applicantIds.size()) return true;

        Long applicantId = applicantIds.get(index);
        Application applicant = applicantMap.get(applicantId);
        List<LocalDateTime> times = availabilityMap.getOrDefault(applicantId, List.of());

        for (LocalDateTime time : times) {
            Position position = applicant.getPosition();
            Long positionId = hasPosition && position != null ? position.getId() : 0L;

            // slotPool 구조 초기화
            slotPool.putIfAbsent(time, new HashMap<>());
            Map<Long, List<TimeSlot>> positionSlotListMap = slotPool.get(time);
            List<TimeSlot> slots = positionSlotListMap.getOrDefault(positionId, new ArrayList<>());

            int usedRooms = slots.size();

            // 기존 슬롯 중 정원이 남은 슬롯이 있는지 확인
            for (TimeSlot slot : slots) {
                long assignedCount = finalAssignment.values().stream()
                        .filter(s -> s.getId().equals(slot.getId()))
                        .count();

                if (assignedCount < config.applicantPerSlot) {
                    finalAssignment.put(applicantId, slot);
                    if (backtrackAssign(index + 1, applicantIds, availabilityMap, applicantMap, slotPool,
                            slotsUsedPerTime, config, interview, finalAssignment, hasPosition, slotMinutes)) {
                        return true;
                    }
                    finalAssignment.remove(applicantId);
                }
            }

            // roomCount 보다 적게 사용 중이면 새 슬롯 생성
            if (usedRooms < config.roomCount) {
                LocalDate date = time.toLocalDate();
                LocalTime start = time.toLocalTime();
                LocalTime end = start.plusMinutes(slotMinutes);
                String roomName = "면접실 " + (usedRooms + 1);

                TimeSlot newSlot = timeSlotRepository.findOrCreate(
                        date, start, end, interview, hasPosition ? position : null, roomName
                );

                slots.add(newSlot);
                positionSlotListMap.put(positionId, slots);
                slotPool.put(time, positionSlotListMap);
                slotsUsedPerTime.put(time, usedRooms + 1);

                finalAssignment.put(applicantId, newSlot);
                if (backtrackAssign(index + 1, applicantIds, availabilityMap, applicantMap, slotPool,
                        slotsUsedPerTime, config, interview, finalAssignment, hasPosition, slotMinutes)) {
                    return true;
                }

                // 롤백
                finalAssignment.remove(applicantId);
                slots.remove(newSlot);
                positionSlotListMap.put(positionId, slots);
                slotPool.put(time, positionSlotListMap);
                slotsUsedPerTime.put(time, usedRooms); // 복원
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
        Short interviewDuration = recruitment.getInterviewDuration();
        List<AvailableTimeRange> timeRanges = recruitment.getAvailableTimeRanges();

        List<TimeSlot> slots = timeSlotRepository.findByInterview(interview);

        Map<LocalDate, List<TimeSlot>> slotsByDate = slots.stream()
                .collect(Collectors.groupingBy(TimeSlot::getDate));

        // 날짜별 InterviewScheduleDTO 생성
        return slotsByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<TimeSlot> daySlots = entry.getValue();

                    // 날짜에 해당하는 available time 찾기
                    AvailableTimeRange timeRange = timeRanges.stream()
                            .filter(r -> r.getDate().equals(date))
                            .findFirst()
                            .orElseThrow(() -> new CustomException(ErrorCode.AVAILABLE_TIME_NOT_EXIST));

                    List<InterviewScheduleDTO.InterviewSlotDTO> slotDTOs = daySlots.stream()
                            .map(InterviewScheduleDTO.InterviewSlotDTO::from)
                            .toList();

                    return InterviewScheduleDTO.from(
                            interviewId,
                            date,
                            timeRange.getStartTime(),
                            timeRange.getEndTime(),
                            interviewDuration,
                            slotDTOs
                    );
                })
                .sorted(Comparator.comparing(InterviewScheduleDTO::date))
                .toList();
    }

    /**
     * 내 면접 시간 조회 (배정된 타임 슬롯 조회)
     * @param interviewId 조회할 면접 ID
     * @param user 현재 로그인 유저
     * @return 조회된 타임 슬롯 반환
     */
    public List<InterviewScheduleDTO.MyInterviewTimeDTO> getMyInterviewTimes(Long interviewId, User user) {
        interviewRepository.getById(interviewId);

        List<TimeSlot> slots = timeSlotRepository.findAllByUserInvolved(interviewId, user);
        return slots.stream()
                .map(InterviewScheduleDTO.MyInterviewTimeDTO::from)
                .toList();
    }


    /**
     * 면접 구성 설정
     */
    public record InterviewConfig(
            int interviewerPerSlot,
            int applicantPerSlot,
            int roomCount
    ) {}
}
