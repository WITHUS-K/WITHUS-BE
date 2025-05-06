package KUSITMS.WITHUS.domain.application.applicationAnswer.repository;

import KUSITMS.WITHUS.domain.application.applicationAnswer.entity.ApplicationAnswer;

import java.util.List;

public interface ApplicationAnswerRepository {
    void saveAll(List<ApplicationAnswer> answers);
}
