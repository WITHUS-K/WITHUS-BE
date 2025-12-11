package KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.organization.organizationRole.repository.OrganizationRoleRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class EvaluationCriteriaAppender {

    private final OrganizationRoleRepository organizationRoleRepository;

    public void appendWithPositions(
            Recruitment recruitment,
            List<EvaluationCriteriaRequestDTO.Create> criteriaList,
            EvaluationType type
    ) {
        if (criteriaList == null) return;

        for (EvaluationCriteriaRequestDTO.Create dto : criteriaList) {
            OrganizationRole organizationRole = getOrganizationRoleIfExistsById(dto.organizationRoleId(), recruitment);

            boolean duplicate = recruitment.getEvaluationCriteriaList().stream().anyMatch(c ->
                    c.getEvaluationType() == type &&
                            c.getContent().equals(dto.content()) &&
                            Objects.equals(c.getOrganizationRole(), organizationRole)
            );
            if (duplicate) continue;

            EvaluationCriteria criteria = EvaluationCriteria.builder()
                    .content(dto.content())
                    .description(dto.description())
                    .evaluationType(type)
                    .organizationRole(organizationRole)
                    .recruitment(recruitment)
                    .build();

            recruitment.addEvaluationCriteria(criteria);
        }
    }

    private OrganizationRole getOrganizationRoleIfExistsById(Long organizationRoleId, Recruitment recruitment) {
        if (organizationRoleId == null) return null;

        // OrganizationRole 조회
        OrganizationRole organizationRole = organizationRoleRepository.getById(organizationRoleId);

        // 공고에 포함된 OrganizationRole인지 검증
        boolean isValidRole = recruitment.getPositions().stream()
                .anyMatch(ror -> ror.getOrganizationRole().getId().equals(organizationRoleId));

        if (!isValidRole) {
            throw new CustomException(ErrorCode.ORGANIZATION_ROLE_NOT_EXIST);
        }

        return organizationRole;
    }
}
