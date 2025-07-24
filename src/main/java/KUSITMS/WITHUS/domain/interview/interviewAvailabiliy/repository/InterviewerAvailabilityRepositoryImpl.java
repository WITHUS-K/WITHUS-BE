package KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.repository;

import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity.InterviewerAvailability;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity.QInterviewerAvailability.interviewerAvailability;

@Repository
@RequiredArgsConstructor
public class InterviewerAvailabilityRepositoryImpl implements InterviewerAvailabilityRepository {

    private final InterviewerAvailabilityJpaRepository availabilityJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<InterviewerAvailability> saveAll(List<InterviewerAvailability> availabilities) {
        return availabilityJpaRepository.saveAll(availabilities);
    }

    @Override
    public List<InterviewerAvailability> findByInterviewId(Long interviewId) {
        return availabilityJpaRepository.findByInterviewId(interviewId);
    }

    @Override
    public boolean existsByInterviewAndUser(Long interviewId, Long userId) {
        Integer result = queryFactory
                .selectOne()
                .from(interviewerAvailability)
                .where(
                        interviewerAvailability.interview.id.eq(interviewId),
                        interviewerAvailability.user.id.eq(userId)
                )
                .fetchFirst();

        return result != null;
    }
}
