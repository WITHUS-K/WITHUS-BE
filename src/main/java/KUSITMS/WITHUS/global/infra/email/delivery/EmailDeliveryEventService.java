package KUSITMS.WITHUS.global.infra.email.delivery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailDeliveryEventService {

    private final EmailDeliveryEventJpaRepository repository;

    public void saveEvents(List<SendGridEventRequest> events) {
        List<EmailDeliveryEvent> entities = events.stream()
                .map(EmailDeliveryEvent::from)
                .toList();

        repository.saveAll(entities);

        entities.forEach(event -> log.info(
                "Email delivery event received: email={} event={} sgMessageId={} reason={} response={}",
                event.getEmail(),
                event.getEvent(),
                event.getSgMessageId(),
                event.getReason(),
                event.getResponse()
        ));
    }
}
