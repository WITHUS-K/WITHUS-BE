package KUSITMS.WITHUS.global.infra.email.delivery;

import KUSITMS.WITHUS.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail/sendgrid/events")
public class SendGridEventWebhookController {

    private final EmailDeliveryEventService service;

    @PostMapping
    public SuccessResponse<String> receiveEvents(@RequestBody List<SendGridEventRequest> events) {
        service.saveEvents(events);
        return SuccessResponse.ok("이메일 이벤트가 저장되었습니다.");
    }
}
