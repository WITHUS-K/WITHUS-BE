package KUSITMS.WITHUS.domain.application.application.service.factory;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.applicationAnswer.dto.ApplicationAnswerRequestDTO;
import KUSITMS.WITHUS.domain.application.applicationAnswer.entity.ApplicationAnswer;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.enumerate.QuestionType;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ApplicationFactory {

    public Application createApplication(ApplicationRequestDTO.Create request, Recruitment recruitment, Position position) {
        return Application.create(
                request.name(),
                request.gender(),
                request.email(),
                request.phoneNumber(),
                request.university(),
                request.major(),
                request.academicStatus(),
                request.birthDate(),
                request.address(),
                recruitment,
                position
        );
    }

    public List<ApplicationAnswer> createAnswers(Application application,
                                                 List<ApplicationAnswerRequestDTO> answerDTOs,
                                                 Map<Long, DocumentQuestion> questionMap,
                                                 Map<String, String> uploadedFileUrls) {
        return answerDTOs.stream()
                .map(dto -> {
                    DocumentQuestion question = questionMap.get(dto.questionId());
                    String fileUrl = null;
                    if (question.getType() == QuestionType.FILE && dto.fileName() != null) {
                        fileUrl = uploadedFileUrls.get(dto.fileName());
                    }
                    return ApplicationAnswer.create(application, question, dto.answerText(), fileUrl);
                })
                .collect(Collectors.toList());
    }
}
