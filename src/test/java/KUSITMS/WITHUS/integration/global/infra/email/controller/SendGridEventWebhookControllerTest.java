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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    @DisplayName("SendGrid 이벤트 웹훅을 인증 없이 수신하고 저장한다")
    void receiveSendGridEventsWithoutAuthentication() throws Exception {
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

        mockMvc.perform(post("/api/v1/mail/sendgrid/events")
                        .contentType(MediaType.APPLICATION_JSON)
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
}
