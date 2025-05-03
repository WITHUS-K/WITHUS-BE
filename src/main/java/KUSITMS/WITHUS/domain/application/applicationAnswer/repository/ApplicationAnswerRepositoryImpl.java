package KUSITMS.WITHUS.domain.application.applicationAnswer.repository;

import KUSITMS.WITHUS.domain.application.applicationAnswer.entity.ApplicationAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationAnswerRepositoryImpl implements ApplicationAnswerRepository{

    private final ApplicationAnswerJpaRepository applicationAnswerJpaRepository;

    @Override
    public void saveAll(List<ApplicationAnswer> answers) {
        applicationAnswerJpaRepository.saveAll(answers);
    }
}
