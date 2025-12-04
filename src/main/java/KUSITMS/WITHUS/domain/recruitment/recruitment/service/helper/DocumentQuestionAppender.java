package KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper;

import KUSITMS.WITHUS.domain.application.applicationAnswer.repository.ApplicationAnswerRepository;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentQuestionAppender {

    private final ApplicationAnswerRepository applicationAnswerRepository;

    public void append(Recruitment recruitment, List<DocumentQuestionRequestDTO.Create> questions) {
        if (questions == null) return;
        recruitment.getQuestions().forEach(q ->
                applicationAnswerRepository.deleteAllByQuestionId(q.getId())
        );
        recruitment.clearDocumentQuestions();

        questions.forEach(q -> {
            OrganizationRole organizationRole = getOrganizationRoleIfExistsByName(q.positionName(), recruitment);

            DocumentQuestion question = DocumentQuestion.builder()
                    .title(q.title())
                    .description(q.description())
                    .type(q.type())
                    .required(q.required())
                    .textLimit(q.textLimit())
                    .includeWhitespace(q.includeWhitespace())
                    .maxFileCount(q.maxFileCount())
                    .maxFileSizeMb(q.maxFileSizeMb())
                    .recruitment(recruitment)
                    .organizationRole(organizationRole)
                    .build();

            recruitment.addDocumentQuestion(question);
        });
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
