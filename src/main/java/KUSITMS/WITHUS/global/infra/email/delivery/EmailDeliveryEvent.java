package KUSITMS.WITHUS.global.infra.email.delivery;

import KUSITMS.WITHUS.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "EMAIL_DELIVERY_EVENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailDeliveryEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMAIL_DELIVERY_EVENT_ID")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String event;

    private String sgMessageId;

    private String sgEventId;

    @Column(length = 1000)
    private String reason;

    @Column(length = 1000)
    private String response;

    private String status;

    private LocalDateTime eventTime;

    private EmailDeliveryEvent(
            String email,
            String event,
            String sgMessageId,
            String sgEventId,
            String reason,
            String response,
            String status,
            LocalDateTime eventTime
    ) {
        this.email = email;
        this.event = event;
        this.sgMessageId = sgMessageId;
        this.sgEventId = sgEventId;
        this.reason = reason;
        this.response = response;
        this.status = status;
        this.eventTime = eventTime;
    }

    public static EmailDeliveryEvent from(SendGridEventRequest event) {
        return new EmailDeliveryEvent(
                event.email(),
                event.event(),
                event.sgMessageId(),
                event.sgEventId(),
                event.reason(),
                event.response(),
                event.status(),
                event.timestamp() == null
                        ? null
                        : LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(event.timestamp()),
                                ZoneId.of("Asia/Seoul")
                        )
        );
    }
}
