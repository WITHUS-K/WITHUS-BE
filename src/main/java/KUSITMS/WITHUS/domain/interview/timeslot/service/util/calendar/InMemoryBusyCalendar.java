package KUSITMS.WITHUS.domain.interview.timeslot.service.util.calendar;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 유저별 바쁜 시간대를 메모리에 보관하여 동시간대 중복 배정을 방지하기 위한 경량 캘린더
 */
public class InMemoryBusyCalendar implements BusyCalendar {

    /** 단일 사용자 바쁜 구간 표현 */
        private record Range(LocalDateTime start, LocalDateTime end) {
    }

    private final Map<Long, List<Range>> busy = new HashMap<>();

    /** 초기화 - 후보 사용자 ID를 키로 등록 */
    public InMemoryBusyCalendar(Collection<Long> userIds) {
        for (Long userId : userIds) busy.put(userId, new ArrayList<>());
    }

    /** 시간 구간 겹침 여부 */
    private static boolean overlap(LocalDateTime start1, LocalDateTime end1,
                                   LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /** 주어진 시간대에 이미 바쁜지 확인 */
    public boolean isBusy(Long userId, LocalDateTime start, LocalDateTime end) {
        for (Range range : busy.get(userId)) {
            if (overlap(range.start, range.end, start, end)) return true;
        }
        return false;
    }

    /** 배정 후 바쁜 시간대 추가 */
    public void markBusy(Long userId, LocalDateTime start, LocalDateTime end) {
        busy.get(userId).add(new Range(start, end));
    }
}
