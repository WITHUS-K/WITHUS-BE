package KUSITMS.WITHUS.domain.evaluation.evaluation.service;

import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.global.infra.email.MailProperties;
import KUSITMS.WITHUS.global.infra.email.sender.MailSender;
import KUSITMS.WITHUS.global.infra.email.template.MailTemplateProvider;
import KUSITMS.WITHUS.global.infra.email.template.MailTemplateType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EvaluationReminderMailService {

    private final RecruitmentRepository recruitmentRepository;
    private final UserRepository userRepository;
    private final MailProperties mailProperties;
    private final MailTemplateProvider templateProvider;
    private final MailSender mailSender;

    public void sendEvaluationReminderMails(Long recruitmentId, List<Long> userIds) {
        Recruitment recruitment = recruitmentRepository.getById(recruitmentId);
        LocalDate dueDate = recruitment.getDocumentResultDate();

        List<User> users = userRepository.findAllById(userIds);

        for (User user : users) {
            Map<String, String> variables = Map.of(
                    "logoUrl", mailProperties.getLogoUrl(),
                    "dueDate", formatKoreanDate(dueDate),
                    "link", mailProperties.getInterviewerAvailabilityUrl()
            );

            String html = templateProvider.loadTemplate(MailTemplateType.EVALUATION_REMINDER, variables);
            mailSender.send(user.getEmail(), "[WITHUS] 평가 마감일 리마인드", html);
        }
    }

    private String formatKoreanDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"));
    }
}
