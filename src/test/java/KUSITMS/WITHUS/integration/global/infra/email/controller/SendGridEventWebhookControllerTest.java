package KUSITMS.WITHUS.integration.global.infra.email.controller;

import KUSITMS.WITHUS.global.infra.email.delivery.EmailDeliveryEventJpaRepository;
import KUSITMS.WITHUS.integration.config.MockInfraBeans;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MockInfraBeans.class)
class SendGridEventWebhookControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EmailDeliveryEventJpaRepository repository;

    private static final KeyPair WEBHOOK_KEY_PAIR = generateKeyPair();

    @DynamicPropertySource
    static void sendGridWebhookProperties(DynamicPropertyRegistry registry) {
        registry.add("mail.sendgrid-event-webhook-public-key", () ->
                Base64.getEncoder().encodeToString(WEBHOOK_KEY_PAIR.getPublic().getEncoded()));
    }

    @Test
    @DisplayName("SendGrid 이벤트 웹훅을 서명 검증 후 저장한다")
    void receiveSendGridEventsWithSignatureVerification() throws Exception {
        String payload = """
                [
                  {
                    "email": "receiver@example.com",
                    "event": "delivered",
                    "sg_message_id": "message-id",
                    "sg_event_id": "event-id",
                    "timestamp": 1513299569,
                    "response": "250 OK"
                  }
                ]
                """;
        String timestamp = "1513299569";

        mockMvc.perform(post("/api/v1/mail/sendgrid/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Twilio-Email-Event-Webhook-Signature", sign(timestamp, payload))
                        .header("X-Twilio-Email-Event-Webhook-Timestamp", timestamp)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("이메일 이벤트가 저장되었습니다."));

        var events = repository.findAll();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getEmail()).isEqualTo("receiver@example.com");
        assertThat(events.get(0).getEvent()).isEqualTo("delivered");
        assertThat(events.get(0).getSgMessageId()).isEqualTo("message-id");
        assertThat(events.get(0).getResponse()).isEqualTo("250 OK");
    }

    @Test
    @DisplayName("같은 sg_event_id 이벤트는 중복 저장하지 않는다")
    void receiveSendGridEventsIdempotently() throws Exception {
        String payload = """
                [
                  {
                    "email": "receiver@example.com",
                    "event": "delivered",
                    "sg_message_id": "message-id",
                    "sg_event_id": "event-id",
                    "timestamp": 1513299569
                  }
                ]
                """;
        String timestamp = "1513299569";

        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/api/v1/mail/sendgrid/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Twilio-Email-Event-Webhook-Signature", sign(timestamp, payload))
                            .header("X-Twilio-Email-Event-Webhook-Timestamp", timestamp)
                            .content(payload))
                    .andExpect(status().isOk());
        }

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("SendGrid 이벤트 웹훅 서명이 유효하지 않으면 저장하지 않는다")
    void rejectSendGridEventsWithInvalidSignature() throws Exception {
        String payload = "[]";
        String timestamp = "1513299569";

        mockMvc.perform(post("/api/v1/mail/sendgrid/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Twilio-Email-Event-Webhook-Signature", "invalid-signature")
                        .header("X-Twilio-Email-Event-Webhook-Timestamp", timestamp)
                        .content(payload))
                .andExpect(status().isUnauthorized());

        assertThat(repository.findAll()).isEmpty();
    }

    private String sign(String timestamp, String payload) throws Exception {
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(WEBHOOK_KEY_PAIR.getPrivate());
        signature.update(timestamp.getBytes(StandardCharsets.UTF_8));
        signature.update(payload.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    private static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(new ECGenParameterSpec("secp256r1"));
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
