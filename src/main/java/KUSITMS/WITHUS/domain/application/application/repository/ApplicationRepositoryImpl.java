package KUSITMS.WITHUS.domain.application.application.repository;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.application.application.entity.QApplication.application;


@Repository
@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepository {

    private final ApplicationJpaRepository applicationJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Application getById(Long id) {
        return applicationJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_EXIST));
    }

    @Override
    public Application save(Application application) {
        return applicationJpaRepository.save(application);
    }

    @Override
    public void delete(Long id) {
        applicationJpaRepository.deleteById(id);
    }

    @Override
    public List<Application> findPassedByRecruitment(Long recruitmentId) {
        return queryFactory
                .selectFrom(application)
                .where(
                        application.recruitment.id.eq(recruitmentId),
                        application.status.eq(ApplicationStatus.DOX_PASS)
                )
                .fetch();
    }

    @Override
    public List<Application> findByRecruitmentId(Long recruitmentId) {
        return applicationJpaRepository.findByRecruitmentId(recruitmentId);
    }

    @Override
    public List<Application> findByRecruitmentIdAndStatusIn(Long recruitmentId, List<ApplicationStatus> statuses) {
        return applicationJpaRepository.findByRecruitmentIdAndStatusIn(recruitmentId, statuses);
    }

    @Override
    public List<Application> findByRecruitment_IdAndPosition_Id(Long recruitmentId, Long positionId) {
        return applicationJpaRepository.findByRecruitment_IdAndPosition_Id(recruitmentId, positionId);
    }

    @Override
    public Long countByRecruitment_IdAndPosition_Id(Long recruitmentId, Long positionId) {
        return applicationJpaRepository.countByRecruitment_IdAndPosition_Id(recruitmentId, positionId);
    }

    @Override
    public List<Application> findAllById(List<Long> longs) {
        return applicationJpaRepository.findAllById(longs);
    }

    @Override
    public List<Application> findDistinctByRecruitment_IdAndEvaluators_Evaluator_IdAndEvaluators_EvaluationType(Long recruitmentId, Long evaluatorId, EvaluationType evaluationType) {
        return applicationJpaRepository.findDistinctByRecruitment_IdAndEvaluators_Evaluator_IdAndEvaluators_EvaluationType(recruitmentId, evaluatorId, evaluationType);
    }
}
