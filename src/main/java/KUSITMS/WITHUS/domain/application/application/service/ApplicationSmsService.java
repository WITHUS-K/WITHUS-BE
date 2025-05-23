package KUSITMS.WITHUS.domain.application.application.service;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.infra.sms.SmsSender;

import java.io.IOException;
import java.util.List;
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

        for (Long applicationId : applicationIds) {
            Application application = applicationRepository.getById(applicationId);
            String to = application.getPhoneNumber();

            if (imageBytes != null) {
                smsSender.sendMms(to, text, imageBytes, filename);
            } else {
                smsSender.send(to, text);
            }

            application.updateIsSmsSent(true);
        }
    }
}
