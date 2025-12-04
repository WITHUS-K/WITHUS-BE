package KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class EvaluationCriteriaAppender {

    public void appendWithPositions(
            Recruitment recruitment,
            List<EvaluationCriteriaRequestDTO.Create> criteriaList,
            EvaluationType type
    ) {
        if (criteriaList == null) return;

        for (EvaluationCriteriaRequestDTO.Create dto : criteriaList) {
            OrganizationRole organizationRole = getOrganizationRoleIfExistsByName(dto.positionName(), recruitment);

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

    private OrganizationRole getOrganizationRoleIfExistsByName(String roleName, Recruitment recruitment) {
        if (roleName == null) return null;

        return recruitment.getPositions().stream()
                .map(ror -> ror.getOrganizationRole())
                .filter(role -> role.getName().equals(roleName))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.POSITION_NOT_EXIST));
    }
}
