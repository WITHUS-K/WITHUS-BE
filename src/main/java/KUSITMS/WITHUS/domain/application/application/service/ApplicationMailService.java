package KUSITMS.WITHUS.domain.application.application.service;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.global.infra.email.sender.MailSender;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

        DateTimeFormatter dateFmt  = DateTimeFormatter.ofPattern("M/d(E)", Locale.KOREA);
        DateTimeFormatter timeFmt  = DateTimeFormatter.ofPattern("HH:mm", Locale.KOREA);

        // 비동기로 여러 수신자에게 전송
        for (Long applicationId : applicationIds) {
            Application application = applicationRepository.getById(applicationId);

            Map<String, String> values = new HashMap<>();

            // name
            values.put("name", application.getName());

            // organizationRole
            String organizationRole = application.getOrganizationRole() != null
                    ? application.getOrganizationRole().getName()
                    : "(불러올 수 없음)";
            values.put("position", organizationRole);

            // interviewDateTime
            TimeSlot ts = application.getTimeSlot();
            String interviewDateTime = "(불러올 수 없음)";
            String interviewRoom     = "(불러올 수 없음)";
            if (ts != null) {
                String datePart  = ts.getDate().format(dateFmt);
                String startPart = ts.getStartTime().format(timeFmt);
                String endPart   = ts.getEndTime().format(timeFmt);
                interviewDateTime = datePart + " " + startPart + "-" + endPart;
                interviewRoom     = ts.getRoomName();
            }
            values.put("interviewDateTime", interviewDateTime);
            values.put("interviewRoom", interviewRoom);

            String message = replaceTemplates(body, values);

            String to = application.getEmail();

            mailSender.sendWithAttachments(to, subject, message, List.copyOf(memFiles));

            application.updateIsMailSent(true);
        }
    }

    private String replaceTemplates(String template, Map<String,String> vars) {
        String result = template;
        for (var entry : vars.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}

