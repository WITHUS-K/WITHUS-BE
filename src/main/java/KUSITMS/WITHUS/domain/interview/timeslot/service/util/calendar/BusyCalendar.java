package KUSITMS.WITHUS.domain.interview.timeslot.service.util.calendar;

import java.time.LocalDateTime;

public interface BusyCalendar {
    boolean isBusy(Long userId, LocalDateTime start, LocalDateTime end);
    void markBusy(Long userId, LocalDateTime start, LocalDateTime end);
}
