package KUSITMS.WITHUS.domain.user.userOrganization.service;

import KUSITMS.WITHUS.domain.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import KUSITMS.WITHUS.domain.user.userOrganization.repository.UserOrganizationRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserOrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserOrganizationRepository userOrganizationRepository;
    private final UserRepository userRepository;

    /**
     * 특정 조직의 운영진을 모두 조회
     * @param organizationId 조회할 조직 ID
     * @return 조회한 사용자들의 정보
     */
    public List<UserResponseDTO.Summary> getAllUsers(Long organizationId) {
        organizationRepository.getById(organizationId);

        return userOrganizationRepository.findUsersByOrganizationId(organizationId).stream()
                .map(UserResponseDTO.Summary::from)
                .toList();
    }

    /**
     * 특정 조직에 특정 사용자를 추가
     * @param organizationId 추가할 조직 ID
     * @param userId 추가할 사용자 ID
     * @return 매핑 정보 반환
     */
    @Transactional
    public UserOrganization addUserToOrganization(Long organizationId, Long userId) {
        Organization organization = organizationRepository.getById(organizationId);
        User user = userRepository.getById(userId);

        boolean alreadyExists = userOrganizationRepository.existsByUserIdAndOrganizationId(userId, organizationId);
        if (alreadyExists) {
            throw new CustomException(ErrorCode.DUPLICATE_USER_ORGANIZATION);
        }

        UserOrganization userOrganization = UserOrganization.builder()
                .user(user)
                .organization(organization)
                .build();

        return userOrganizationRepository.save(userOrganization);
    }
}
