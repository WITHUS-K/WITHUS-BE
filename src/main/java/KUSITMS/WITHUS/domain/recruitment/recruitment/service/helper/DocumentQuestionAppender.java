package KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper;

import KUSITMS.WITHUS.domain.application.applicationAnswer.repository.ApplicationAnswerRepository;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.organization.organizationRole.repository.OrganizationRoleRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DocumentQuestionAppender {

    private final ApplicationAnswerRepository applicationAnswerRepository;
    private final OrganizationRoleRepository organizationRoleRepository;

    public void append(Recruitment recruitment, List<DocumentQuestionRequestDTO.Create> questions) {
        if (questions == null) return;
        
        // 같은 order가 중복되는지 검증
        validateQuestionOrders(questions);
        
        recruitment.getQuestions().forEach(q ->
                applicationAnswerRepository.deleteAllByQuestionId(q.getId())
        );
        recruitment.clearDocumentQuestions();

        questions.forEach(q -> {
            OrganizationRole organizationRole = getOrganizationRoleIfExistsById(q.organizationRoleId(), recruitment);

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
                    .order(q.order())
                    .build();

            recruitment.addDocumentQuestion(question);
        });
    }

    private void validateQuestionOrders(List<DocumentQuestionRequestDTO.Create> questions) {
        if (questions == null || questions.isEmpty()) return;
        
        Set<Integer> orderSet = questions.stream()
                .map(DocumentQuestionRequestDTO.Create::order)
                .collect(Collectors.toSet());
        
        if (orderSet.size() != questions.size()) {
            throw new CustomException(ErrorCode.DUPLICATE_QUESTION_ORDER);
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
