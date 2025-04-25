package KUSITMS.WITHUS.domain.interview.interview.repository;

import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewRepositoryImpl implements InterviewRepository {

    private final InterviewJpaRepository interviewJpaRepository;

    @Override
    public Interview getById(Long interviewId) {
        return interviewJpaRepository.findById(interviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_EXIST));
    }

    @Override
    public Interview save(Interview interview) {
        return interviewJpaRepository.save(interview);
    }
}
