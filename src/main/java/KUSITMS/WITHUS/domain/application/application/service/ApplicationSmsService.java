package KUSITMS.WITHUS.domain.application.application.service;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.interview.timeslot.entity.TimeSlot;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.infra.sms.SmsSender;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationSmsService {

    private final SmsSender smsSender;
    private final ApplicationRepository applicationRepository;

    /**
     * 여러 사용자에게 SMS/MMS를 일괄 발송합니다.
     * @param applicationIds 지원서 Id 리스트
     * @param text 본문 메시지
     * @param attachment MMS용 이미지 파일 (없으면 순수 SMS)
     */
    public void sendBulkSms(
            List<Long> applicationIds,
            String text,
            MultipartFile attachment
    ) {
        byte[] imageBytes = null;
        String filename = null;
        if (attachment != null && !attachment.isEmpty()) {
            try {
                imageBytes = attachment.getBytes();
                filename   = attachment.getOriginalFilename();
            } catch (IOException e) {
                throw new CustomException(ErrorCode.SMS_ATTACHMENT_PROCESS_FAIL);
            }
        }

        DateTimeFormatter dateFmt  = DateTimeFormatter.ofPattern("M/d(E)", Locale.KOREA);
        DateTimeFormatter timeFmt  = DateTimeFormatter.ofPattern("HH:mm", Locale.KOREA);

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

            String message = replaceTemplates(text, values);

            String to = application.getPhoneNumber();

            if (imageBytes != null) {
                smsSender.sendMms(to, message, imageBytes, filename);
            } else {
                smsSender.send(to, message);
            }

            application.updateIsSmsSent(true);
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
