package KUSITMS.WITHUS.domain.application.application.service;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.global.infra.email.sender.MailSender;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationMailService {

    private final MailSender mailSender;
    private final ApplicationRepository applicationRepository;

    /**
     * 여러 수신자에게 제목·본문·첨부파일(메모리 복사본) 메일을 발송합니다.
     */
    public void sendBulkMail(
            List<Long> applicationIds,
            String subject,
            String body,
            List<MultipartFile> attachments
    ) throws MessagingException {
        List<ByteArrayResource> memFiles = (attachments == null)
                ? List.of()
                : attachments.stream()
                .map(f -> {
                    try {
                        return new ByteArrayResource(f.getBytes()) {
                            @Override public String getFilename() {
                                return f.getOriginalFilename();
                            }
                        };
                    } catch (IOException e) {
                        throw new RuntimeException("첨부파일 처리 실패", e);
                    }
                })
                .collect(Collectors.toList());

        // 비동기로 여러 수신자에게 전송
        for (Long applicationId : applicationIds) {
            Application application = applicationRepository.getById(applicationId);
            String to = application.getEmail();

            mailSender.sendWithAttachments(to, subject, body, List.copyOf(memFiles));

            application.updateIsMailSent(true);
        }
    }
}

