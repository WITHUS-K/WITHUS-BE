package KUSITMS.WITHUS.domain.application.applicationEvaluator.repository;

import KUSITMS.WITHUS.domain.application.applicationEvaluator.entity.ApplicationEvaluator;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.application.applicationEvaluator.entity.QApplicationEvaluator.applicationEvaluator;

@Repository
@RequiredArgsConstructor
public class ApplicationEvaluatorRepositoryImpl implements ApplicationEvaluatorRepository {

    private final ApplicationEvaluatorJpaRepository applicationEvaluatorJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteAllByApplication_Recruitment_Id(Long recruitmentId) {
        applicationEvaluatorJpaRepository.deleteAllByApplication_Recruitment_Id(recruitmentId);
    }

    @Override
    public void deleteAllByApplication_Id(Long applicationId) {
        applicationEvaluatorJpaRepository.deleteAllByApplication_Id(applicationId);
    }

    @Override
    public List<ApplicationEvaluator> findByRecruitmentAndPositionAndType(Long recruitmentId, Long positionId, EvaluationType type) {
        return queryFactory
                .selectFrom(applicationEvaluator)
                .where(
                        applicationEvaluator.application.recruitment.id.eq(recruitmentId),
                        applicationEvaluator.application.position.id.eq(positionId),
                        applicationEvaluator.evaluationType.eq(type)
                )
                .fetch();
    }

    @Override
    public List<ApplicationEvaluator> findByEvaluatorAndRecruitmentAndType(Long evaluatorId, EvaluationType type, Long recruitmentId) {
        return queryFactory
                .selectFrom(applicationEvaluator)
                .where(
                        applicationEvaluator.evaluator.id.eq(evaluatorId),
                        applicationEvaluator.evaluationType.eq(type),
                        applicationEvaluator.application.recruitment.id.eq(recruitmentId)
                )
                .fetch();
    }

    @Override
    public void saveAll(List<ApplicationEvaluator> assigns) {
        applicationEvaluatorJpaRepository.saveAll(assigns);
    }
}
