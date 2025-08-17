package KUSITMS.WITHUS.domain.interview.timeslot.service.util.selection.policy;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;

@Component
public class LeastAssignedFirstPolicy implements FairnessPolicy {

    /** 현재까지 적게 배정된 사람 우선, 동률이면 userId 오름차순 */
    @Override
    public Comparator<Long> comparator(final Map<Long, Integer> assignedCount) {
        return Comparator
                .comparingInt((Long userId) -> assignedCount.get(userId))
                .thenComparingLong(Long::longValue);
    }
}
