package KUSITMS.WITHUS.domain.user.userOrganization.service;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.userOrganization.dto.UserOrganizationResponseDTO;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import KUSITMS.WITHUS.domain.user.userOrganization.repository.UserOrganizationRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.infra.email.MailProperties;
import KUSITMS.WITHUS.global.infra.email.sender.MailSender;
import KUSITMS.WITHUS.global.infra.email.template.MailTemplateProvider;
import KUSITMS.WITHUS.global.infra.email.template.MailTemplateType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserOrganizationServiceImpl implements UserOrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserOrganizationRepository userOrganizationRepository;
    private final UserRepository userRepository;
    private final MailProperties mailProperties;
    private final MailTemplateProvider templateProvider;
    private final MailSender mailSender;

    /**
     * 특정 조직의 운영진을 모두 조회 - 페이지네이션
     * @param organizationId 조회할 조직 ID
     * @return 조회한 사용자들의 정보
     */
    @Override
    public Page<UserResponseDTO.DetailProfile> getUsers(Long organizationId, int page, int size) {
        organizationRepository.getById(organizationId);

        Pageable pageable = PageRequest.of(page, size);
        return userOrganizationRepository.findByOrganizationId(organizationId, pageable)
                .map(user -> UserResponseDTO.DetailProfile.from(user, organizationId));
    }

    /**
     * 특정 조직에 특정 사용자를 추가
     * @param organizationId 추가할 조직 ID
     * @param userIds 추가할 사용자 ID 리스트
     * @return 매핑 정보 반환
     */
    @Override
    @Transactional
    public List<UserOrganizationResponseDTO.Detail> addUserToOrganization(Long organizationId, List<Long> userIds) {
        Organization organization = organizationRepository.getById(organizationId);

        List<UserOrganizationResponseDTO.Detail> result = new ArrayList<>();

        for (Long userId : userIds) {
            if (userOrganizationRepository.existsByUserIdAndOrganizationId(userId, organizationId)) {
                continue;
            }

            User user = userRepository.getById(userId);

            UserOrganization userOrganization = UserOrganization.builder()
                    .user(user)
                    .organization(organization)
                    .build();

            user.addUserOrganization(userOrganization);
            organization.addUserOrganization(userOrganization);

            UserOrganization saved = userOrganizationRepository.save(userOrganization);
            result.add(UserOrganizationResponseDTO.Detail.from(saved));
        }

        return result;
    }

    /**
     * 사용자 일괄 삭제
     * @param organizationId 사용자가 속한 조직 ID
     * @param userIds 삭제할 사용자 ID 리스트
     */
    @Override
    @Transactional
    public void removeUsers(Long organizationId, List<Long> userIds) {
        List<UserOrganization> targets = userOrganizationRepository
                .findAllByOrganizationIdAndUserIdIn(organizationId, userIds);

        if (targets.size() != userIds.size()) {
            throw new CustomException(ErrorCode.USER_ORGANIZATION_NOT_FOUND);
        }

        userOrganizationRepository.deleteAllInBatch(targets);
    }

    /**
     * 조직에 속한 운영진 전체 조회,  keyword, roleId가 있다면 검색
     * @param organizationId 조회할 조직의 ID
     * @param keyword 검색어(이름)
     * @return 조회된 유저 정보
     */
    @Override
    public List<UserResponseDTO.SummaryForSearch> getUsersWithAssignment(Long organizationId, String keyword, Long roleId) {
        List<User> users = userOrganizationRepository.findUsersByOrganizationAndKeyword(organizationId, keyword);

        return users.stream()
                .map(user -> {
                    boolean assigned = user.getUserOrganizationRoles().stream()
                            .anyMatch(r -> r.getOrganizationRole().getId().equals(roleId));
                    return UserResponseDTO.SummaryForSearch.from(user, assigned);
                })
                .toList();
    }

    /**
     * 초대 메일 전송
     * @param organizationId 대상 조직 ID
     * @param userIds 초대를 보낼 운영진 ID 리스트
     * @param inviterName 초대자(로그인 유저) 이름
     */
    @Override
    public void sendInvitationEmails(Long organizationId, List<Long> userIds, String inviterName) {
        Organization org = organizationRepository.getById(organizationId);
        List<User> users = userRepository.findAllById(userIds);

        for (User user : users) {
            Map<String, String> variables = Map.of(
                    "logoUrl", mailProperties.getLogoUrl(),
                    "organizationName", org.getName(),
                    "inviterName", inviterName,
                    "link", mailProperties.getInterviewerAvailabilityUrl()
            );

            String html = templateProvider.loadTemplate(MailTemplateType.INVITATION, variables);
            mailSender.send(user.getEmail(), "[WITHUS] 조직 초대 메일", html);
        }
    }
}
