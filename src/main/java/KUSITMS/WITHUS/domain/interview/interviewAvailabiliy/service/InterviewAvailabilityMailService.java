package KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.service;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.userOrganization.repository.UserOrganizationRepository;
import KUSITMS.WITHUS.global.infra.email.MailProperties;
import KUSITMS.WITHUS.global.infra.email.MailSender;
import KUSITMS.WITHUS.global.infra.email.MailTemplateProvider;
import KUSITMS.WITHUS.global.infra.email.MailTemplateType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InterviewAvailabilityMailService {

    private final MailSender mailSender;
    private final MailTemplateProvider templateProvider;
    private final MailProperties mailProperties;
    private final UserOrganizationRepository userOrganizationRepository;
    private final OrganizationRepository organizationRepository;

    public void sendMailToUsers(Long organizationId) {
        Organization organization = organizationRepository.getById(organizationId);
        List<User> users = userOrganizationRepository.findListByOrganizationId(organizationId);

        for (User user : users) {
            Map<String, String> variables = Map.of(
                    "link", mailProperties.getInterviewerAvailabilityUrl(),
                    "organization", organization.getName(),
                    "logoUrl", mailProperties.getLogoUrl()
            );
            String html = templateProvider.loadTemplate(MailTemplateType.INTERVIEW_REQUEST, variables);
            mailSender.send(user.getEmail(), "[WITHUS] 면접 가능 시간 입력 요청", html);
        }
    }
}
