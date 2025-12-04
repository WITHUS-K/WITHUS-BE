package KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper;

import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.organization.organizationRole.repository.OrganizationRoleRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PositionAppender {

    private final OrganizationRoleRepository organizationRoleRepository;

    public void append(Recruitment recruitment, List<Long> organizationRoleIds) {
        if (organizationRoleIds == null || organizationRoleIds.isEmpty()) return;

        Long organizationId = recruitment.getOrganization().getId();

        organizationRoleIds.stream()
                .distinct()
                .forEach(roleId -> {
                    // OrganizationRole 조회
                    OrganizationRole role = organizationRoleRepository.getById(roleId);

                    // 조직에 속한 OrganizationRole인지 검증
                    if (!role.getOrganization().getId().equals(organizationId)) {
                        throw new CustomException(ErrorCode.ORGANIZATION_ROLE_NOT_EXIST);
                    }

                    // 이미 추가된 역할인지 확인
                    boolean alreadyExists = recruitment.getPositions().stream()
                            .anyMatch(ror -> ror.getOrganizationRole().getId().equals(role.getId()));

                    if (!alreadyExists) {
                        recruitment.addOrganizationRole(role);
                    }
                });
    }
}
