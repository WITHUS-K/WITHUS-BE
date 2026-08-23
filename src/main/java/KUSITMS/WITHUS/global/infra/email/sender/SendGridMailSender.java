package KUSITMS.WITHUS.global.infra.email.sender;

import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.infra.email.MailProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Profile("!test")
@ConditionalOnProperty(name = "mail.provider", havingValue = "sendgrid")
@RequiredArgsConstructor
public class SendGridMailSender implements MailSender {

    private static final int ACCEPTED = 202;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final MailProperties mailProperties;

    @Override
    public void send(String to, String subject, String text) {
        sendMailAfterCommit(to, subject, text, List.of());
    }

    @Override
    public void sendWithAttachments(
            String to,
            String subject,
            String html,
            List<InputStreamSource> attachments
    ) throws MessagingException {
        sendMailAfterCommit(to, subject, html, attachments);
    }

    private void sendMailAfterCommit(String to, String subject, String html, List<InputStreamSource> attachments) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            sendMail(to, subject, html, attachments);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    sendMail(to, subject, html, attachments);
                } catch (CustomException e) {
                    log.error("SendGrid email send failed after transaction commit: [{}] subject: {}", to, subject, e);
                }
            }
        });
    }

    private void sendMail(String to, String subject, String html, List<InputStreamSource> attachments) {
        validateProperties();

        try {
            String requestBody = objectMapper.writeValueAsString(createRequestBody(to, subject, html, attachments));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(mailProperties.getSendgridEndpoint()))
                    .timeout(Duration.ofSeconds(10))
                    .header("Authorization", "Bearer " + mailProperties.getSendgridApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            long startedAt = System.nanoTime();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long elapsedMs = (System.nanoTime() - startedAt) / 1_000_000L;

            if (response.statusCode() != ACCEPTED) {
                log.error(
                        "SendGrid rejected email in {}ms: status={} to={} subject={} body={}",
                        elapsedMs,
                        response.statusCode(),
                        to,
                        subject,
                        response.body()
                );
                throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
            }

            String messageId = response.headers().firstValue("X-Message-Id").orElse("unknown");
            log.info(
                    "Email accepted by SendGrid in {}ms: [{}] subject: {} messageId: {}",
                    elapsedMs,
                    to,
                    subject,
                    messageId
            );
        } catch (CustomException e) {
            throw e;
        } catch (IOException e) {
            log.error("Failed to build SendGrid email request: [{}] subject: {}", to, subject, e);
            throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("SendGrid email request interrupted: [{}] subject: {}", to, subject, e);
            throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
        }
    }

    private Map<String, Object> createRequestBody(
            String to,
            String subject,
            String html,
            List<InputStreamSource> attachments
    ) throws IOException {
        Map<String, Object> body = Map.of(
                "from", Map.of(
                        "email", mailProperties.getFromEmail(),
                        "name", mailProperties.getFromName()
                ),
                "personalizations", List.of(
                        Map.of("to", List.of(Map.of("email", to)))
                ),
                "subject", subject,
                "content", List.of(Map.of(
                        "type", "text/html",
                        "value", html
                ))
        );

        if (attachments == null || attachments.isEmpty()) {
            return body;
        }

        return Map.of(
                "from", body.get("from"),
                "personalizations", body.get("personalizations"),
                "subject", subject,
                "content", body.get("content"),
                "attachments", attachments.stream()
                        .map(this::toAttachment)
                        .toList()
        );
    }

    private Map<String, Object> toAttachment(InputStreamSource source) {
        try {
            String encoded = Base64.getEncoder().encodeToString(source.getInputStream().readAllBytes());
            String filename = source instanceof org.springframework.core.io.ByteArrayResource resource && resource.getFilename() != null
                    ? resource.getFilename()
                    : "attachment";

            return Map.of(
                    "content", encoded,
                    "filename", filename,
                    "type", "application/octet-stream",
                    "disposition", "attachment"
            );
        } catch (IOException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
        }
    }

    private void validateProperties() {
        if (isBlank(mailProperties.getSendgridApiKey())
                || isBlank(mailProperties.getFromEmail())
                || isBlank(mailProperties.getSendgridEndpoint())) {
            log.error("SendGrid mail properties are missing");
            throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
