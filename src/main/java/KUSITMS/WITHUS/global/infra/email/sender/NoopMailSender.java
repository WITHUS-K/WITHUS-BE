package KUSITMS.WITHUS.global.infra.email.sender;

import KUSITMS.WITHUS.global.infra.email.MailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/**
 * 실제 발송 없이 발송 지연만 재현하는 구현체.
 * 부하 테스트에서 SendGrid/Gmail 일일 한도를 소모하지 않기 위해 사용한다.
 *
 * <p>{@link SmtpMailSender}, {@link SendGridMailSender} 와 동일하게 커밋 이후에 동작한다.
 * 트랜잭션 안에서 지연을 주면 DB 커넥션 점유 시간이 함께 늘어나 전혀 다른 것을 측정하게 되므로
 * afterCommit 구조를 반드시 맞춰야 한다.
 */
@Slf4j
@Component
@Profile("!test")
@ConditionalOnProperty(name = "mail.provider", havingValue = "noop")
@RequiredArgsConstructor
public class NoopMailSender implements MailSender {

    private final MailProperties mailProperties;

    @Override
    public void send(String to, String subject, String text) {
        simulateAfterCommit(to, subject);
    }

    @Override
    public void sendWithAttachments(
            String to,
            String subject,
            String html,
            List<InputStreamSource> attachments
    ) {
        simulateAfterCommit(to, subject);
    }

    private void simulateAfterCommit(String to, String subject) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            simulate(to, subject);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                simulate(to, subject);
            }
        });
    }

    private void simulate(String to, String subject) {
        long delayMs = mailProperties.getNoopDelayMs();

        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        log.info("Email skipped by noop provider (simulated {}ms): [{}] subject: {}", delayMs, to, subject);
    }
}
