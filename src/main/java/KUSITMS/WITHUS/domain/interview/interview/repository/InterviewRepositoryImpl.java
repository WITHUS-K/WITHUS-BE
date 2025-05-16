package KUSITMS.WITHUS.domain.interview.interview.repository;

import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static KUSITMS.WITHUS.domain.interview.interview.entity.QInterview.interview;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewRepositoryImpl implements InterviewRepository {

    private final InterviewJpaRepository interviewJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Interview getById(Long interviewId) {
        return interviewJpaRepository.findById(interviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_EXIST));
    }

    @Override
    @Transactional
    public Interview save(Interview interview) {
        return interviewJpaRepository.save(interview);
    }

    @Override
    public List<Interview> findAllByRecruitmentIdIn(List<Long> recruitmentIds) {
        if (recruitmentIds == null || recruitmentIds.isEmpty()) {
            return Collections.emptyList();
        }

        return queryFactory.selectFrom(interview)
                .where(interview.recruitment.id.in(recruitmentIds))
                .fetch();
    }
}
