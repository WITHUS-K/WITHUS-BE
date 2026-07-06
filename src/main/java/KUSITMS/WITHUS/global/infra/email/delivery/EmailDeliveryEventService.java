package KUSITMS.WITHUS.global.infra.email.delivery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailDeliveryEventService {

    private final EmailDeliveryEventJpaRepository repository;

    public void saveEvents(List<SendGridEventRequest> events) {
        Set<String> requestedEventIds = events.stream()
                .map(SendGridEventRequest::sgEventId)
                .filter(sgEventId -> sgEventId != null && !sgEventId.isBlank())
                .collect(Collectors.toSet());
        Set<String> existingEventIds = requestedEventIds.isEmpty()
                ? Set.of()
                : repository.findAllBySgEventIdIn(requestedEventIds).stream()
                .map(EmailDeliveryEvent::getSgEventId)
                .collect(Collectors.toSet());
        Set<String> seenEventIds = new HashSet<>();

        List<EmailDeliveryEvent> entities = events.stream()
                .filter(event -> isNewEvent(event, existingEventIds, seenEventIds))
                .map(EmailDeliveryEvent::from)
                .toList();

        if (entities.isEmpty()) {
            return;
        }

        repository.saveAll(entities);

        entities.forEach(event -> log.info(
                "Email delivery event received: email={} event={} sgMessageId={} reason={} response={}",
                maskEmail(event.getEmail()),
                event.getEvent(),
                event.getSgMessageId(),
                event.getReason(),
                event.getResponse()
        ));
    }

    private boolean isNewEvent(SendGridEventRequest event, Set<String> existingEventIds, Set<String> seenEventIds) {
        String sgEventId = event.sgEventId();
        if (sgEventId == null || sgEventId.isBlank()) {
            return true;
        }

        return !existingEventIds.contains(sgEventId) && seenEventIds.add(sgEventId);
    }

    private String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "";
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return "***" + email.substring(Math.max(atIndex, 0));
        }

        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}
