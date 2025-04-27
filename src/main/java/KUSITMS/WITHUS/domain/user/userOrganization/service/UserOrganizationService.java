package KUSITMS.WITHUS.domain.user.userOrganization.service;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import KUSITMS.WITHUS.domain.user.userOrganization.repository.UserOrganizationRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
     * 특정 조직의 운영진을 모두 조회 - 페이지네이션
     * @param organizationId 조회할 조직 ID
     * @param page 조회할 페이지
     * @param size 페이지 크기
     * @return 조회한 사용자들의 정보
     */
    public Page<UserResponseDTO.DetailForOrganization> getUsers(Long organizationId, int page, int size) {
        organizationRepository.getById(organizationId);

        Pageable pageable = PageRequest.of(page, size);
        return userOrganizationRepository.findByOrganizationId(organizationId, pageable)
                .map(UserResponseDTO.DetailForOrganization::from);
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

        user.addUserOrganization(userOrganization);
        organization.addUserOrganization(userOrganization);

        return userOrganizationRepository.save(userOrganization);
    }

    @Transactional
    public void removeUsers(Long organizationId, List<Long> userIds) {
        List<UserOrganization> targets = userOrganizationRepository
                .findAllByOrganizationIdAndUserIdIn(organizationId, userIds);

        if (targets.size() != userIds.size()) {
            throw new CustomException(ErrorCode.USER_ORGANIZATION_NOT_FOUND);
        }

        userOrganizationRepository.deleteAllInBatch(targets);
    }

    public List<UserResponseDTO.Summary> getAllUsersByOrganization(Long organizationId, String keyword) {
        return userOrganizationRepository.findManagersByOrganizationId(organizationId, keyword).stream()
                .map(UserResponseDTO.Summary::from)
                .toList();
    }
}
