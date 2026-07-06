package KUSITMS.WITHUS.domain.organization.organizationRole.service;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.organization.organizationRole.dto.OrganizationRoleResponseDTO;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.organization.organizationRole.repository.OrganizationRoleRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import KUSITMS.WITHUS.domain.user.userOrganization.repository.UserOrganizationRepository;
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
    private final UserOrganizationRepository userOrganizationRepository;

    /**
     * 운영진에게 역할 일괄 추가/제거
     * @param organizationId 역할이 속한 조직 ID
     * @param userId 역할을 부여할 사용자 ID
     * @param roleIds 부여할 역할 ID 리스트
     * @return 부여된 역할 정보 반환
     */
    @Override
    @Transactional
    public List<OrganizationRoleResponseDTO.DetailForUser> assignRoleToUser(Long organizationId, Long userId, List<Long> roleIds) {
        final List<Long> requestedRoleIds = normalizeRequestedIds(roleIds);

        User user = userRepository.getById(userId);
        List<UserOrganizationRole> currentLinksInOrg = getCurrentLinksInOrg(user, organizationId);
        Set<Long> currentRoleIds = toRoleIdSet(currentLinksInOrg);

        Map<Long, OrganizationRole> requestedRoleMapInOrg = loadRequestedRoleMapInOrg(organizationId, requestedRoleIds);
        Set<Long> requestedValidIds = requestedRoleMapInOrg.keySet();

        Set<Long> toAddIds = calcToAddIds(currentRoleIds, requestedValidIds);
        Set<Long> toRemoveIds = calcToRemoveIds(currentRoleIds, requestedValidIds);

        removeLinks(user, currentLinksInOrg, toRemoveIds);
        if (!requestedValidIds.isEmpty()) {
            ensureUserOrganizationMembership(user, organizationId);
        }
        addLinks(user, requestedRoleMapInOrg, toAddIds);

        return user.getUserOrganizationRoles().stream()
                .filter(uor -> uor.getOrganizationRole().getOrganization().getId().equals(organizationId))
                .map(OrganizationRoleResponseDTO.DetailForUser::from)
                .toList();
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
        OrganizationRole role = getValidatedRole(organizationId, roleId);
        List<UserOrganizationRole> currentAssignments = role.getUserOrganizationRoles();

        removeUnmatchedAssignments(role, newUserIds, currentAssignments);
        List<UserOrganizationRole> newAssignments = addNewAssignments(role, newUserIds, currentAssignments);

        return buildResponse(currentAssignments, newAssignments, newUserIds);
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

    /**
     * 역할 삭제
     * @param organizationId 삭제할 역할이 속한 조직 ID
     * @param roleId 삭제할 역할 ID
     */
    @Override
    @Transactional
    public void deleteRole(Long organizationId, Long roleId) {
        OrganizationRole role = organizationRoleRepository.getById(roleId);

        // 조직 일치 검증
        if (!role.getOrganization().getId().equals(organizationId)) {
            throw new CustomException(ErrorCode.ORGANIZATION_ROLE_ORG_MISMATCH);
        }

        userOrganizationRoleRepository.deleteByOrganizationRoleId(roleId);
        organizationRoleRepository.deleteById(roleId);
    }

    private OrganizationRole getValidatedRole(Long organizationId, Long roleId) {
        OrganizationRole role = organizationRoleRepository.getById(roleId);
        if (!role.getOrganization().getId().equals(organizationId)) {
            throw new CustomException(ErrorCode.ORGANIZATION_ROLE_ORG_MISMATCH);
        }
        return role;
    }

    private void removeUnmatchedAssignments(OrganizationRole role, List<Long> newUserIds, List<UserOrganizationRole> currentAssignments) {
        Set<Long> newUserIdSet = new HashSet<>(newUserIds);

        List<UserOrganizationRole> toRemove = currentAssignments.stream()
                .filter(assignment -> !newUserIdSet.contains(assignment.getUser().getId()))
                .toList();
        userOrganizationRoleRepository.deleteAll(toRemove);

        Set<Long> removedUserIds = toRemove.stream()
                .map(assignment -> assignment.getUser().getId())
                .collect(Collectors.toSet());
        role.getUserOrganizationRoles().removeIf(assignment ->
                removedUserIds.contains(assignment.getUser().getId()));
    }

    private List<UserOrganizationRole> addNewAssignments(OrganizationRole role, List<Long> newUserIds, List<UserOrganizationRole> currentAssignments) {
        Set<Long> existingUserIds = currentAssignments.stream()
                .map(assignment -> assignment.getUser().getId())
                .collect(Collectors.toSet());

        List<Long> toAddUserIds = newUserIds.stream()
                .filter(id -> !existingUserIds.contains(id))
                .toList();

        List<User> toAddUsers = userRepository.findAllById(toAddUserIds);
        List<UserOrganizationRole> newAssignments = new ArrayList<>();

        for (User user : toAddUsers) {
            ensureUserOrganizationMembership(user, role.getOrganization().getId());
            UserOrganizationRole assignment = UserOrganizationRole.assign(user, role);
            user.addUserOrganizationRole(assignment);
            role.addUserOrganizationRole(assignment);
            newAssignments.add(assignment);
        }

        userOrganizationRoleRepository.saveAll(newAssignments);
        return newAssignments;
    }

    private List<OrganizationRoleResponseDTO.DetailForUser> buildResponse(
            List<UserOrganizationRole> currentAssignments,
            List<UserOrganizationRole> newAssignments,
            List<Long> newUserIds
    ) {
        Set<Long> userIdSet = new HashSet<>(newUserIds);
        Map<Long, OrganizationRoleResponseDTO.DetailForUser> resultMap = new LinkedHashMap<>();

        currentAssignments.stream()
                .filter(assignment -> userIdSet.contains(assignment.getUser().getId()))
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

    private List<Long> normalizeRequestedIds(List<Long> roleIds) {
        return (roleIds == null) ? Collections.emptyList() : roleIds.stream().distinct().toList();
    }

    private List<UserOrganizationRole> getCurrentLinksInOrg(User user, Long organizationId) {
        return user.getUserOrganizationRoles().stream()
                .filter(uor -> uor.getOrganizationRole() != null
                        && uor.getOrganizationRole().getOrganization().getId().equals(organizationId))
                .toList();
    }

    private Set<Long> toRoleIdSet(List<UserOrganizationRole> links) {
        return links.stream()
                .map(uor -> uor.getOrganizationRole().getId())
                .collect(Collectors.toSet());
    }

    private Map<Long, OrganizationRole> loadRequestedRoleMapInOrg(Long organizationId, List<Long> requestedRoleIds) {
        if (requestedRoleIds == null || requestedRoleIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<Long> requestedIdSet = new HashSet<>(requestedRoleIds);
        List<OrganizationRole> requestedRoles = organizationRoleRepository.findAllById(requestedRoleIds);

        if (requestedRoles.size() != requestedIdSet.size()) {
            throw new CustomException(ErrorCode.ORGANIZATION_ROLE_NOT_EXIST);
        }

        Map<Long, OrganizationRole> roleMap = requestedRoles.stream()
                .filter(r -> r.getOrganization().getId().equals(organizationId))
                .collect(Collectors.toMap(OrganizationRole::getId, r -> r));

        // 요청한 모든 ID가 동일 조직에 속해야 함
        if (roleMap.size() != requestedIdSet.size()) {
            throw new CustomException(ErrorCode.ORGANIZATION_ROLE_ORG_MISMATCH);
        }

        return roleMap;
    }

    private Set<Long> calcToAddIds(Set<Long> currentRoleIds, Set<Long> requestedValidIds) {
        Set<Long> toAdd = new HashSet<>(requestedValidIds);
        toAdd.removeAll(currentRoleIds);
        return toAdd;
    }

    private Set<Long> calcToRemoveIds(Set<Long> currentRoleIds, Set<Long> requestedValidIds) {
        Set<Long> toRemove = new HashSet<>(currentRoleIds);
        toRemove.removeAll(requestedValidIds);
        return toRemove;
    }

    private void removeLinks(User user, List<UserOrganizationRole> currentLinksInOrg, Set<Long> toRemoveIds) {
        if (toRemoveIds.isEmpty()) return;

        List<UserOrganizationRole> toRemoveLinks = currentLinksInOrg.stream()
                .filter(link -> toRemoveIds.contains(link.getOrganizationRole().getId()))
                .toList();

        userOrganizationRoleRepository.deleteAll(toRemoveLinks);
        user.getUserOrganizationRoles().removeAll(toRemoveLinks);
        toRemoveLinks.forEach(link -> link.getOrganizationRole().getUserOrganizationRoles().remove(link));
    }

    private void addLinks(User user, Map<Long, OrganizationRole> requestedRoleMapInOrg, Set<Long> toAddIds) {
        if (toAddIds.isEmpty()) return;

        List<UserOrganizationRole> toAddLinks = new ArrayList<>();
        for (Long addId : toAddIds) {
            OrganizationRole role = requestedRoleMapInOrg.get(addId);
            UserOrganizationRole link = UserOrganizationRole.assign(user, role);
            user.addUserOrganizationRole(link);
            role.addUserOrganizationRole(link);
            toAddLinks.add(link);
        }
        userOrganizationRoleRepository.saveAll(toAddLinks);
    }

    private void ensureUserOrganizationMembership(User user, Long organizationId) {
        if (userOrganizationRepository.existsByUserIdAndOrganizationId(user.getId(), organizationId)) {
            return;
        }

        Organization organization = organizationRepository.getById(organizationId);
        UserOrganization userOrganization = UserOrganization.builder()
                .user(user)
                .organization(organization)
                .build();

        user.addUserOrganization(userOrganization);
        organization.addUserOrganization(userOrganization);
        userOrganizationRepository.save(userOrganization);
    }
}
