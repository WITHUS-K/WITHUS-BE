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

import java.util.*;
import java.util.stream.Collectors;

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
    public OrganizationRoleResponseDTO.Detail createRole(Long organizationId, String name, String color) {
        Organization organization = organizationRepository.getById(organizationId);

        boolean exists = organizationRoleRepository.existsByOrganizationIdAndName(organizationId, name);
        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_ORGANIZATION_ROLE_NAME);
        }

        OrganizationRole role = OrganizationRole.builder()
                .name(name)
                .color(color)
                .build();

        organization.addOrganizationRole(role);
        organizationRoleRepository.save(role);

        return OrganizationRoleResponseDTO.Detail.from(role);
    }

    /**
     * 조직의 역할 전체 조회
     * @param organizationId 조회할 조직 ID
     * @param keyword 필터링할 검색어
     * @return 조회된 역할 정보
     */
    @Override
    public OrganizationRoleResponseDTO.DetailForOrganization getOrganizationRoles(Long organizationId, String keyword) {
        List<OrganizationRole> roles = organizationRoleRepository.findByOrganizationIdAndKeyword(organizationId, keyword);

        List<OrganizationRoleResponseDTO.RoleDetail> roleDetails = roles.stream()
                .map(OrganizationRoleResponseDTO.RoleDetail::from)
                .toList();

        return OrganizationRoleResponseDTO.DetailForOrganization.from(roleDetails);
    }

    /**
     * 특정 역할에 운영진 일괄 추가
     * @param organizationId 역할이 속한 조직 ID
     * @param roleId 추가할 역할 ID
     * @param newUserIds 추가될 사용자 ID 리스트
     * @return 추가된 정보 반환
     */
    @Override
    @Transactional
    public List<OrganizationRoleResponseDTO.DetailForUser> updateUsersOfRole(Long organizationId, Long roleId, List<Long> newUserIds) {
        OrganizationRole role = organizationRoleRepository.getById(roleId);

        if (!role.getOrganization().getId().equals(organizationId)) {
            throw new CustomException(ErrorCode.ORGANIZATION_ROLE_ORG_MISMATCH);
        }

        Set<Long> newUserIdSet = new HashSet<>(newUserIds);
        List<UserOrganizationRole> currentAssignments = role.getUserOrganizationRoles();

        // 삭제할 사용자 역할 매핑 제거
        List<UserOrganizationRole> toRemove = currentAssignments.stream()
                .filter(assignment -> !newUserIdSet.contains(assignment.getUser().getId()))
                .toList();
        userOrganizationRoleRepository.deleteAll(toRemove);

        Set<Long> removedUserIds = toRemove.stream()
                .map(assignment -> assignment.getUser().getId())
                .collect(Collectors.toSet());
        role.getUserOrganizationRoles().removeIf(
                assignment -> removedUserIds.contains(assignment.getUser().getId())
        );

        // 기존 유지되는 사용자 ID 추출
        Set<Long> existingUserIds = currentAssignments.stream()
                .map(assignment -> assignment.getUser().getId())
                .collect(Collectors.toSet());

        // 새롭게 추가할 사용자 ID 필터링
        List<Long> toAddUserIds = newUserIds.stream()
                .filter(id -> !existingUserIds.contains(id))
                .toList();
        List<User> toAddUsers = userRepository.findAllById(toAddUserIds);

        // 새로운 사용자 역할 매핑 생성
        List<UserOrganizationRole> newAssignments = new ArrayList<>();
        for (User user : toAddUsers) {
            UserOrganizationRole userOrgRole = UserOrganizationRole.assign(user, role);
            user.addUserOrganizationRole(userOrgRole);
            role.addUserOrganizationRole(userOrgRole);
            newAssignments.add(userOrgRole);
        }

        userOrganizationRoleRepository.saveAll(newAssignments);

        // 최종 응답
        Map<Long, OrganizationRoleResponseDTO.DetailForUser> resultMap = new LinkedHashMap<>();

        currentAssignments.stream()
                .filter(assignment -> newUserIdSet.contains(assignment.getUser().getId()))
                .forEach(assignment -> resultMap.put(
                        assignment.getUser().getId(),
                        OrganizationRoleResponseDTO.DetailForUser.from(assignment)
                ));

        newAssignments.forEach(assignment ->
                resultMap.put(
                        assignment.getUser().getId(),
                        OrganizationRoleResponseDTO.DetailForUser.from(assignment)
                )
        );

        return new ArrayList<>(resultMap.values());
    }

    /**
     * 역할 수정
     * @param organizationId 수정할 역할이 속한 조직 ID
     * @param roleId 수정할 역할 ID
     * @param name 수정할 이름
     * @param color 수정할 색상
     */
    @Override
    @Transactional
    public void updateRole(Long organizationId, Long roleId, String name, String color) {
        OrganizationRole role = organizationRoleRepository.getById(roleId);

        if (!role.getOrganization().getId().equals(organizationId)) {
            throw new CustomException(ErrorCode.ORGANIZATION_ROLE_ORG_MISMATCH);
        }

        boolean nameConflict = organizationRoleRepository.existsByOrganizationIdAndNameExceptId(organizationId, name, roleId);
        if (nameConflict) {
            throw new CustomException(ErrorCode.DUPLICATE_ORGANIZATION_ROLE_NAME);
        }

        role.update(name, color);
    }

}