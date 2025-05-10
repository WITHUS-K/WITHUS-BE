package KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper;

import KUSITMS.WITHUS.domain.application.applicationAnswer.repository.ApplicationAnswerRepository;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
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
            Position position = getPositionIfExistsByName(q.positionName(), recruitment);

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
                    .position(position)
                    .build();

            recruitment.addDocumentQuestion(question);
        });
    }

    private Position getPositionIfExistsByName(String positionName, Recruitment recruitment) {
        if (positionName == null) return null;

        return recruitment.getPositions().stream()
                .filter(p -> p.getName().equals(positionName))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.POSITION_NOT_EXIST));
    }
}
