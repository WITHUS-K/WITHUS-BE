package KUSITMS.WITHUS.domain.application.applicationAnswer.repository;

import KUSITMS.WITHUS.domain.application.applicationAnswer.entity.ApplicationAnswer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static KUSITMS.WITHUS.domain.application.applicationAnswer.entity.QApplicationAnswer.applicationAnswer;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationAnswerRepositoryImpl implements ApplicationAnswerRepository{

    private final ApplicationAnswerJpaRepository applicationAnswerJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public void saveAll(List<ApplicationAnswer> answers) {
        applicationAnswerJpaRepository.saveAll(answers);
    }

    @Override
    @Transactional
    public void deleteAllByQuestionId(Long id) {
        queryFactory
                .delete(applicationAnswer)
                .where(applicationAnswer.question.id.eq(id))
                .execute();
    }
}
