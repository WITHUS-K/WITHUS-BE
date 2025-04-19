package KUSITMS.WITHUS.domain.interview.interview.service;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.application.availability.entity.ApplicantAvailability;
import KUSITMS.WITHUS.domain.application.availability.repository.ApplicantAvailabilityRepository;
import KUSITMS.WITHUS.domain.application.position.entity.Position;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewScheduleDTO;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.interview.repository.InterviewRepository;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslot.repository.TimeSlotRepository;
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

        // 3. 각 시간대별로 포지션에 따라 타임슬롯을 나누고, 해당 시간에 생성된 슬롯 개수도 함께 관리
        Map<LocalDateTime, Map<Long, TimeSlot>> slotPool = new HashMap<>();
        Map<LocalDateTime, Integer> slotsUsedPerTime = new HashMap<>();
        for (ApplicantAvailability avail : availabilityList) {
            slotPool.putIfAbsent(avail.getAvailableTime(), new HashMap<>());
        }

        // 4. 가능한 시간 적은 지원자 우선 배정
        List<Long> applicantIds = new ArrayList<>(availabilityMap.keySet());
        applicantIds.sort(Comparator.comparingInt(id -> availabilityMap.get(id).size()));

        // 5. 백트래킹으로 전체 배정 시도
        Map<Long, TimeSlot> finalAssignment = new HashMap<>();
        boolean success = backtrackAssign(0, applicantIds, availabilityMap, applicantMap, slotPool, slotsUsedPerTime, config, interview, finalAssignment);

        // 6. 배정 결과 저장
        if (success) {
            finalAssignment.forEach((applicantId, slot) -> {
                Application app = applicantMap.get(applicantId);
                app.assignTimeSlot(slot);
                Long positionId = app.getPosition().getId();
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
            Map<LocalDateTime, Map<Long, TimeSlot>> slotPool,
            Map<LocalDateTime, Integer> slotsUsedPerTime,
            InterviewConfig config,
            Interview interview,
            Map<Long, TimeSlot> finalAssignment
    ) {
        if (index == applicantIds.size()) return true;

        Long applicantId = applicantIds.get(index);
        Application applicant = applicantMap.get(applicantId);
        List<LocalDateTime> times = availabilityMap.getOrDefault(applicantId, List.of());

        for (LocalDateTime time : times) {
            Map<Long, TimeSlot> positionSlots = slotPool.get(time);
            int usedSlots = slotsUsedPerTime.getOrDefault(time, 0);

            Position position = applicant.getPosition();
            Long positionId = position.getId();
            TimeSlot currentSlot = positionSlots.get(positionId);

            // 이미 생성된 슬롯 존재 -> 인원 수 확인 후 배정
            if (currentSlot != null) {
                Long currentSlotId = currentSlot.getId();
                long assignedCount = finalAssignment.values().stream()
                        .filter(s -> s.getId().equals(currentSlotId))
                        .count();

                if (assignedCount < config.applicantPerSlot) {
                    finalAssignment.put(applicantId, currentSlot);
                    if (backtrackAssign(index + 1, applicantIds, availabilityMap, applicantMap, slotPool, slotsUsedPerTime, config, interview, finalAssignment)) return true;
                    finalAssignment.remove(applicantId);
                }
            }

            // 새 슬롯 생성 가능할 경우 -> 생성 후 배정
            else if (usedSlots < config.roomCount) {
                LocalDate date = time.toLocalDate();
                LocalTime start = time.toLocalTime();
                LocalTime end = start.plusMinutes(config.slotMinutes);

                TimeSlot newSlot = timeSlotRepository.findOrCreate(date, start, end, interview, position);
                positionSlots.put(positionId, newSlot);
                slotsUsedPerTime.put(time, usedSlots + 1);

                finalAssignment.put(applicantId, newSlot);
                if (backtrackAssign(index + 1, applicantIds, availabilityMap, applicantMap, slotPool, slotsUsedPerTime, config, interview, finalAssignment)) return true;

                // 실패 시 롤백
                finalAssignment.remove(applicantId);
                positionSlots.remove(positionId);
                slotsUsedPerTime.put(time, usedSlots);
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

        List<TimeSlot> slots = timeSlotRepository.findByInterview(interview);

        return slots.stream()
                .map(InterviewScheduleDTO::from)
                .toList();
    }

    /**
     * 면접 구성 설정
     */
    public record InterviewConfig(
            int interviewerPerSlot,
            int applicantPerSlot,
            int roomCount,
            int slotMinutes
    ) {}
}
