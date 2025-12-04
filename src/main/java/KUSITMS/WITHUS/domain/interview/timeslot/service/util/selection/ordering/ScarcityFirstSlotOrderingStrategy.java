package KUSITMS.WITHUS.domain.interview.timeslot.service.util.selection.ordering;

import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.domain.interview.timeslot.service.util.candidate.CandidateIndex;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ScarcityFirstSlotOrderingStrategy implements SlotOrderingStrategy {

    /**
     * 후보 수가 적은 슬롯부터 정렬
     * 동률이면 날짜 - 시간 - 방 이름 순으로 정렬
     */
    @Override
    public List<TimeSlot> order(final List<TimeSlot> timeSlots, final CandidateIndex index) {
        return timeSlots.stream()
                .sorted(Comparator
                        .comparingInt((TimeSlot slot) -> countEligibleCandidates(slot, index))
                        .thenComparing(TimeSlot::getDate)
                        .thenComparing(TimeSlot::getStartTime)
                        .thenComparing(TimeSlot::getRoomName))
                .toList();
    }

    /** 주어진 슬롯에 배정 가능한 사용자 수를 계산 */
    private int countEligibleCandidates(final TimeSlot slot, final CandidateIndex index) {
        final LocalDateTime slotStart = slot.getDate().atTime(slot.getStartTime());
        final OrganizationRole slotOrganizationRole = slot.getOrganizationRole();

        int eligibleCount = 0;

        for (Map.Entry<Long, Set<LocalDateTime>> entry : index.getAvailableTimes().entrySet()) {
            final Long userId = entry.getKey();
            final Set<LocalDateTime> availableAt = entry.getValue();

            // 해당 시각 가능 여부
            if (!availableAt.contains(slotStart)) continue;

            // 사용자 객체 조회
            final User candidate = index.getUserMap().get(userId);
            if (candidate == null) continue;

            // 역할 매칭 - 역할이 없으면 통과
            final boolean roleMatches = (slotOrganizationRole == null) || candidate.hasMatchingRole(slotOrganizationRole);
            if (roleMatches) eligibleCount++;
        }

        return eligibleCount;
    }
}
