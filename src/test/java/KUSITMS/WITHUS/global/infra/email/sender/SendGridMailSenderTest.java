package KUSITMS.WITHUS.global.infra.email.sender;

import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.infra.email.MailProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SendGridMailSenderTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("SendGrid Mail Send API로 Bearer 인증 헤더와 메일 본문을 전송한다")
    void sendUsesSendGridMailSendApi() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse<String> response = mockResponse(202, "");
        AtomicReference<HttpRequest> capturedRequest = new AtomicReference<>();

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenAnswer(invocation -> {
                    capturedRequest.set(invocation.getArgument(0));
                    return response;
                });

        SendGridMailSender sender = new SendGridMailSender(httpClient, objectMapper, properties());

        sender.send("receiver@example.com", "제목", "<p>본문</p>");

        HttpRequest request = capturedRequest.get();
        assertThat(request.uri().toString()).isEqualTo("https://api.sendgrid.com/v3/mail/send");
        assertThat(request.headers().firstValue("Authorization")).contains("Bearer SG.test-key");
        assertThat(request.headers().firstValue("Content-Type")).contains("application/json");

        String body = request.bodyPublisher()
                .flatMap(this::readBody)
                .orElseThrow();
        JsonNode json = objectMapper.readTree(body);

        assertThat(json.at("/from/email").asText()).isEqualTo("noreply@withus.test");
        assertThat(json.at("/from/name").asText()).isEqualTo("WITHUS");
        assertThat(json.at("/personalizations/0/to/0/email").asText()).isEqualTo("receiver@example.com");
        assertThat(json.at("/subject").asText()).isEqualTo("제목");
        assertThat(json.at("/content/0/type").asText()).isEqualTo("text/html");
        assertThat(json.at("/content/0/value").asText()).isEqualTo("<p>본문</p>");
    }

    @Test
    @DisplayName("SendGrid가 202가 아닌 응답을 반환하면 메일 전송 실패로 처리한다")
    void sendFailsWhenSendGridDoesNotAcceptMessage() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse<String> response = mockResponse(400, "{\"errors\":[{\"message\":\"bad request\"}]}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        SendGridMailSender sender = new SendGridMailSender(httpClient, objectMapper, properties());

        assertThatThrownBy(() -> sender.send("receiver@example.com", "제목", "<p>본문</p>"))
                .isInstanceOf(CustomException.class);

        verify(httpClient).send(
                any(HttpRequest.class),
                argThat(handler -> handler != null)
        );
    }

    private MailProperties properties() {
        MailProperties properties = new MailProperties();
        properties.setProvider("sendgrid");
        properties.setFromEmail("noreply@withus.test");
        properties.setFromName("WITHUS");
        properties.setSendgridApiKey("SG.test-key");
        properties.setSendgridEndpoint("https://api.sendgrid.com/v3/mail/send");
        return properties;
    }

    private HttpResponse<String> mockResponse(int statusCode, String body) {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn(body);
        when(response.headers()).thenReturn(HttpHeaders.of(Map.of(), (name, value) -> true));
        return response;
    }

    private Optional<String> readBody(HttpRequest.BodyPublisher publisher) {
        var subscriber = new BodyCaptureSubscriber();
        publisher.subscribe(subscriber);
        return Optional.of(subscriber.body());
    }

    private static class BodyCaptureSubscriber implements Flow.Subscriber<ByteBuffer> {
        private final List<ByteBuffer> buffers = new ArrayList<>();
        private final CountDownLatch done = new CountDownLatch(1);
        private Flow.Subscription subscription;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ByteBuffer item) {
            buffers.add(item);
        }

        @Override
        public void onError(Throwable throwable) {
            done.countDown();
        }

        @Override
        public void onComplete() {
            done.countDown();
        }

        private String body() {
            try {
                done.await(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            int length = buffers.stream().mapToInt(ByteBuffer::remaining).sum();
            byte[] bytes = new byte[length];
            int offset = 0;
            for (ByteBuffer buffer : buffers) {
                int remaining = buffer.remaining();
                buffer.get(bytes, offset, remaining);
                offset += remaining;
            }

            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}
