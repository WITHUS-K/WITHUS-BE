package KUSITMS.WITHUS.domain.recruitment.recruitment.service.validator;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class RecruitmentValidator {

    public void validateUserBelongsToOrganization(boolean isMember) {
        if (!isMember) {
            throw new CustomException(ErrorCode.USER_ORGANIZATION_NOT_FOUND);
        }
    }

    public void validateRecruitmentRequest(RecruitmentRequestDTO.Upsert request) {
        if (request.organizationId() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        if (request.title() == null || request.title().isBlank()) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        if (request.documentDeadline() == null || request.documentResultDate() == null || request.finalResultDate() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
    }

    public void validateOrganizationExists(Organization organization) {
        if (organization == null) {
            throw new CustomException(ErrorCode.ORGANIZATION_NOT_EXIST);
        }
    }
}
