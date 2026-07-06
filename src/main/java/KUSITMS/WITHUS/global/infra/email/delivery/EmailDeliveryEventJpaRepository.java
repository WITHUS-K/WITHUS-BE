package KUSITMS.WITHUS.global.infra.email.delivery;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface EmailDeliveryEventJpaRepository extends JpaRepository<EmailDeliveryEvent, Long> {
    List<EmailDeliveryEvent> findAllBySgEventIdIn(Collection<String> sgEventIds);
}
