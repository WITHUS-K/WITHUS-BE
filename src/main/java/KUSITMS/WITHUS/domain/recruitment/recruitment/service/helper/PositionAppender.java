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

    public void append(Recruitment recruitment, List<String> roleNames) {
        if (roleNames == null) return;

        Long organizationId = recruitment.getOrganization().getId();

        roleNames.stream()
                .distinct()
                .forEach(roleName -> {
                    // 조직에 속한 OrganizationRole만 사용 가능
                    List<OrganizationRole> roles = organizationRoleRepository.findByOrganizationIdAndKeyword(organizationId, roleName);
                    OrganizationRole role = roles.stream()
                            .filter(r -> r.getName().equals(roleName))
                            .findFirst()
                            .orElseThrow(() -> new CustomException(ErrorCode.ORGANIZATION_ROLE_NOT_EXIST));

                    // 이미 추가된 역할인지 확인
                    boolean alreadyExists = recruitment.getPositions().stream()
                            .anyMatch(ror -> ror.getOrganizationRole().getId().equals(role.getId()));

                    if (!alreadyExists) {
                        recruitment.addOrganizationRole(role);
                    }
                });
    }
}
