package KUSITMS.WITHUS.domain.interview.timeslot.service.util.candidate;

import KUSITMS.WITHUS.domain.interview.timeslot.service.util.calendar.BusyCalendar;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CandidateSelector {

    /** 1차 후보 - 시간/포지션 일치 */
    public List<Long> preliminaryCandidates(final LocalDateTime slotStart,
                                            final Position slotPosition,
                                            final CandidateIndex index) {
        final List<Long> prelim = new ArrayList<>();

        for (final Map.Entry<Long, Set<LocalDateTime>> entry : index.getAvailableTimes().entrySet()) {
            final Long userId = entry.getKey();
            final boolean hasTime = entry.getValue().contains(slotStart);

            if (!hasTime) {
                log.debug("[FAIL] userId={} 는 시간 불일치", userId);
                continue;
            }

            final User user = index.getUserMap().get(userId);
            final boolean matchRole = (slotPosition == null) || user.hasMatchingRole(slotPosition);

            if (!matchRole) {
                log.debug("[FAIL] userId={} ({}) 포지션 불일치", userId, user.getName());
                log.debug("유저 역할 목록: {}", user.getUserOrganizationRoles().stream()
                        .map(r -> r.getOrganizationRole().getName())
                        .toList());
                continue;
            }

            prelim.add(userId);
        }
        return prelim;
    }

    /** 2차 후보 - 동시간대 중복 배정 제거 */
    public List<Long> feasibleCandidates(final List<Long> prelim,
                                         final LocalDateTime slotStart,
                                         final LocalDateTime slotEnd,
                                         final CandidateIndex index,
                                         final BusyCalendar busyCalendar) {
        final List<Long> feasible = new ArrayList<>();

        for (final Long userId : prelim) {
            if (busyCalendar.isBusy(userId, slotStart, slotEnd)) {
                final User user = index.getUserMap().get(userId);
                log.debug("[FAIL] userId={} ({}) 이미 배정됨", userId, user.getName());
                continue;
            }
            feasible.add(userId);
        }
        return feasible;
    }
}
