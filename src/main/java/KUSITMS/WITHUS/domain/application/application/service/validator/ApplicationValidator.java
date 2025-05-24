package KUSITMS.WITHUS.domain.application.application.service.validator;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.applicationAnswer.dto.ApplicationAnswerRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.enumerate.QuestionType;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ApplicationValidator {

    public void validateRequiredFields(Recruitment recruitment, ApplicationRequestDTO.Create request) {
        if (recruitment.isNeedGender() && request.gender() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        if (recruitment.isNeedSchool() && request.university() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        if (recruitment.isNeedBirthDate() && request.birthDate() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        if (recruitment.isNeedAcademicStatus() && request.major() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        if (recruitment.isNeedAcademicStatus() && request.academicStatus() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        if (recruitment.isNeedAddress() && request.address() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
    }

    public void validateFileAnswers(List<ApplicationAnswerRequestDTO> answers,
                                    List<MultipartFile> files,
                                    List<DocumentQuestion> questions) {

        Map<Long, DocumentQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(DocumentQuestion::getId, q -> q));

        Set<String> providedFileNames = files != null
                ? files.stream().map(MultipartFile::getOriginalFilename).collect(Collectors.toSet())
                : Set.of();

        for (var answer : answers) {
            DocumentQuestion question = questionMap.get(answer.questionId());
            if (question.getType() == QuestionType.FILE) {
                if (answer.fileName() == null || !providedFileNames.contains(answer.fileName())) {
                    throw new CustomException(ErrorCode.FILE_NAME_NOT_MATCH);
                }
            }
        }

        long expectedFileCount = answers.stream()
                .filter(a -> questionMap.get(a.questionId()).getType() == QuestionType.FILE)
                .count();

        assert files != null;
        if (files.size() != expectedFileCount) {
            throw new CustomException(ErrorCode.FILE_COUNT_MISMATCH);
        }
    }
}
