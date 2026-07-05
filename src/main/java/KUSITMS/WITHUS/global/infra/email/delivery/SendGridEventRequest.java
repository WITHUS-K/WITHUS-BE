package KUSITMS.WITHUS.global.infra.email.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendGridEventRequest(
        String email,
        String event,
        @JsonProperty("sg_message_id") String sgMessageId,
        @JsonProperty("sg_event_id") String sgEventId,
        Long timestamp,
        String reason,
        String response,
        String status
) {
}
