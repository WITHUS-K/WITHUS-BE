package KUSITMS.WITHUS.domain.organization.organizationRole.service;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.organization.organizationRole.dto.OrganizationRoleResponseDTO;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.organization.organizationRole.repository.OrganizationRoleRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.userOrganizationRole.entity.UserOrganizationRole;
import KUSITMS.WITHUS.domain.user.userOrganizationRole.repository.UserOrganizationRoleRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrganizationRoleServiceImpl implements OrganizationRoleService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationRoleRepository organizationRoleRepository;
    private final UserOrganizationRoleRepository userOrganizationRoleRepository;

    @Override
    @Transactional
    public List<OrganizationRoleResponseDTO.DetailForUser> assignRoleToUser(Long organizationId, Long userId, List<Long> roleIds) {
        User user = userRepository.getById(userId);
        List<OrganizationRole> roles = organizationRoleRepository.findAllById(roleIds);

        List<OrganizationRoleResponseDTO.DetailForUser> result = new ArrayList<>();

        for (OrganizationRole role : roles) {
            if (!role.getOrganization().getId().equals(organizationId)) {
                continue; // 조직 ID 불일치
            }

            boolean alreadyAssigned = user.getUserOrganizationRoles().stream()
                    .anyMatch(r -> r.getOrganizationRole().getId().equals(role.getId()));

            if (alreadyAssigned) {
                continue; // 중복 역할
            }

            UserOrganizationRole userOrgRole = UserOrganizationRole.assign(user, role);

            user.addUserOrganizationRole(userOrgRole);
            role.addUserOrganizationRole(userOrgRole);

            userOrganizationRoleRepository.save(userOrgRole);

            result.add(OrganizationRoleResponseDTO.DetailForUser.from(userOrgRole));
        }

        return result;
    }

    /**
     * 조직에 역할을 추가
     * @param organizationId 추가할 조직
     * @param name 추가할 역할의 이름
     * @return 생성된 역할의 정보
     */
    @Override
    @Transactional
    public OrganizationRoleResponseDTO.Detail createRole(Long organizationId, String name) {
        Organization organization = organizationRepository.getById(organizationId);

        boolean exists = organizationRoleRepository.existsByOrganizationIdAndName(organizationId, name);
        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_ORGANIZATION_ROLE_NAME);
        }

        OrganizationRole role = OrganizationRole.builder()
                .name(name)
                .build();

        organization.addOrganizationRole(role);
        organizationRoleRepository.save(role);

        return OrganizationRoleResponseDTO.Detail.from(role);
    }
}