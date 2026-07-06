package KUSITMS.WITHUS.domain.organization.organizationRoleGroup.service;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.organization.organizationRole.repository.OrganizationRoleRepository;
import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.dto.OrganizationRoleGroupRequestDTO;
import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.dto.OrganizationRoleGroupResponseDTO;
import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.entity.OrganizationRoleGroup;
import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.repository.OrganizationRoleGroupJpaRepository;
import KUSITMS.WITHUS.domain.user.userOrganization.repository.UserOrganizationRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationRoleGroupServiceImpl implements OrganizationRoleGroupService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationRoleRepository organizationRoleRepository;
    private final OrganizationRoleGroupJpaRepository organizationRoleGroupJpaRepository;
    private final UserOrganizationRepository userOrganizationRepository;

    @Override
    @Transactional
    public OrganizationRoleGroupResponseDTO.Detail create(Long userId, Long organizationId, OrganizationRoleGroupRequestDTO.Create request) {
        validateOrganizationAccess(userId, organizationId);
        validateSelectionCount(request.selectionMinCount(), request.selectionMaxCount());

        Organization organization = organizationRepository.getById(organizationId);
        OrganizationRoleGroup group = OrganizationRoleGroup.create(
                request.name(),
                request.selectionMinCount(),
                request.selectionMaxCount(),
                organization
        );

        return OrganizationRoleGroupResponseDTO.Detail.from(organizationRoleGroupJpaRepository.save(group));
    }

    @Override
    @Transactional
    public OrganizationRoleGroupResponseDTO.Detail assignRoles(Long userId, Long organizationId, Long groupId, OrganizationRoleGroupRequestDTO.AssignRoles request) {
        validateOrganizationAccess(userId, organizationId);
        OrganizationRoleGroup group = getGroupInOrganization(groupId, organizationId);
        List<Long> distinctRoleIds = request.roleIds().stream().distinct().toList();
        if (distinctRoleIds.size() != request.roleIds().size()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        List<OrganizationRole> roles = organizationRoleRepository.findAllById(distinctRoleIds);

        if (roles.size() != distinctRoleIds.size()) {
            throw new CustomException(ErrorCode.ORGANIZATION_ROLE_NOT_EXIST);
        }

        Set<Long> requestedRoleIds = distinctRoleIds.stream().collect(Collectors.toSet());
        List<OrganizationRole> currentRoles = List.copyOf(group.getOrganizationRoles());
        currentRoles.stream()
                .filter(role -> !requestedRoleIds.contains(role.getId()))
                .forEach(role -> role.assignGroup(null));

        for (OrganizationRole role : roles) {
            if (!role.getOrganization().getId().equals(organizationId)) {
                throw new CustomException(ErrorCode.ORGANIZATION_ROLE_ORG_MISMATCH);
            }
            role.assignGroup(group);
        }

        return OrganizationRoleGroupResponseDTO.Detail.from(group);
    }

    @Override
    public List<OrganizationRoleGroupResponseDTO.Detail> getGroups(Long userId, Long organizationId) {
        validateOrganizationAccess(userId, organizationId);
        organizationRepository.getById(organizationId);
        return organizationRoleGroupJpaRepository.findByOrganizationIdOrderByIdAsc(organizationId).stream()
                .map(OrganizationRoleGroupResponseDTO.Detail::from)
                .toList();
    }

    private OrganizationRoleGroup getGroupInOrganization(Long groupId, Long organizationId) {
        OrganizationRoleGroup group = organizationRoleGroupJpaRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORGANIZATION_ROLE_NOT_EXIST));

        if (!group.getOrganization().getId().equals(organizationId)) {
            throw new CustomException(ErrorCode.ORGANIZATION_ROLE_ORG_MISMATCH);
        }

        return group;
    }

    private void validateSelectionCount(int min, int max) {
        if (min < 0 || max < 1 || min > max) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }

    private void validateOrganizationAccess(Long userId, Long organizationId) {
        if (!userOrganizationRepository.existsByUserIdAndOrganizationId(userId, organizationId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}
