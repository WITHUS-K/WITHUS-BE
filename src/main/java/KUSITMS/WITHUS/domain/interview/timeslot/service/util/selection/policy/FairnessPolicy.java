package KUSITMS.WITHUS.domain.interview.timeslot.service.util.selection.policy;

import java.util.Comparator;
import java.util.Map;

public interface FairnessPolicy {
    /** 공평 분배 정책 */
    Comparator<Long> comparator(Map<Long, Integer> assignedCount);
}
