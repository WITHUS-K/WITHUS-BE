package KUSITMS.WITHUS.global.infra.email.delivery;

import KUSITMS.WITHUS.global.response.SuccessResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail/sendgrid/events")
public class SendGridEventWebhookController {

    private final EmailDeliveryEventService service;
    private final SendGridEventWebhookVerifier verifier;
    private final ObjectMapper objectMapper;

    @PostMapping
    public SuccessResponse<String> receiveEvents(
            @RequestBody byte[] payload,
            @RequestHeader("X-Twilio-Email-Event-Webhook-Signature") String signature,
            @RequestHeader("X-Twilio-Email-Event-Webhook-Timestamp") String timestamp
    ) throws Exception {
        verifier.verify(signature, timestamp, payload);
        List<SendGridEventRequest> events = objectMapper.readValue(payload, new TypeReference<>() {
        });
        service.saveEvents(events);
        return SuccessResponse.ok("이메일 이벤트가 저장되었습니다.");
    }
}
