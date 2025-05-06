package KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper;

import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentQuestionAppender {

    public void append(Recruitment recruitment, List<DocumentQuestionRequestDTO.Create> questions) {
        if (questions == null) return;

        questions.forEach(q -> {
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
                    .build();

            recruitment.addDocumentQuestion(question);
        });
    }
}
