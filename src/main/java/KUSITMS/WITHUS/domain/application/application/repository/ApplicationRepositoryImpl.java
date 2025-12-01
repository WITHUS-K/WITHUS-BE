package KUSITMS.WITHUS.domain.application.application.repository;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
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
import static KUSITMS.WITHUS.domain.interview.timeslot.entity.QTimeSlot.timeSlot;
import static KUSITMS.WITHUS.domain.recruitment.position.entity.QPosition.position;
import static KUSITMS.WITHUS.domain.user.user.entity.QUser.user;


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

    @Override
    public Long countByRecruitmentIdAndStatusIn(Long recruitmentId, List<ApplicationStatus> statuses) {
        return applicationJpaRepository.countByRecruitmentIdAndStatusIn(recruitmentId, statuses);
    }

    @Override
    public List<ApplicationResponseDTO.CandidateDTO> findEligibleCandidates(
            Long recruitmentId,
            Long timeSlotId,   // 현재 타임슬롯 ID
            String q,
            boolean excludeCurrent // true: 어떤 타임슬롯에도 미배정만, false: 현재 타임슬롯 배정은 허용
    ) {
        String keyword = (q == null || q.isBlank()) ? null : q.trim();

        // 이름 검색
        com.querydsl.core.types.dsl.BooleanExpression namePredicate =
                (keyword == null) ? null : application.name.containsIgnoreCase(keyword);

        // 배정 제외 조건
        com.querydsl.core.types.dsl.BooleanExpression assignmentPredicate =
                excludeCurrent
                        // 어떤 타임슬롯에도 배정되지 않은 지원자만
                        ? application.timeSlot.isNull()
                        // 미배정 또는 "현재" 타임슬롯에만 배정된 지원자 허용
                        : application.timeSlot.isNull().or(application.timeSlot.id.eq(timeSlotId));

        return queryFactory
                .select(com.querydsl.core.types.Projections.constructor(
                        ApplicationResponseDTO.CandidateDTO.class,
                        application.id,
                        application.name
                ))
                .from(application)
                .where(
                        application.recruitment.id.eq(recruitmentId),
                        application.status.eq(ApplicationStatus.DOX_PASS),
                        assignmentPredicate,
                        namePredicate
                )
                .orderBy(application.name.asc())
                .fetch();
    }

    @Override
    public List<Application> findForTimeSlot(Long timeSlotId) {
        return queryFactory.selectFrom(application).distinct()
                .join(application.timeSlot, timeSlot).fetchJoin()
                .leftJoin(application.position, position).fetchJoin()
                .leftJoin(application.user, user).fetchJoin()
                .where(timeSlot.id.eq(timeSlotId))
                .orderBy(application.createdAt.asc())
                .fetch();
    }

    @Override
    public Long findPreviousIdInRecruitment(Long recruitmentId, Long currentId) {
        return queryFactory
                .select(application.id.max())
                .from(application)
                .where(
                        application.recruitment.id.eq(recruitmentId),
                        application.id.lt(currentId)
                )
                .fetchOne();
    }

    @Override
    public Long findNextIdInRecruitment(Long recruitmentId, Long currentId) {
        return queryFactory
                .select(application.id.min())
                .from(application)
                .where(
                        application.recruitment.id.eq(recruitmentId),
                        application.id.gt(currentId)
                )
                .fetchOne();
    }
}
