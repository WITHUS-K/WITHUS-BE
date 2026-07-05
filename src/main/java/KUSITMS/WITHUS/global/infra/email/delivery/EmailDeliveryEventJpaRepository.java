package KUSITMS.WITHUS.global.infra.email.delivery;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailDeliveryEventJpaRepository extends JpaRepository<EmailDeliveryEvent, Long> {
}
