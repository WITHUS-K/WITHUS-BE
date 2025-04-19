package KUSITMS.WITHUS.domain.interview.interview.service;

import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.interview.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;

    @Transactional
    public Long create() {
        Interview interview = Interview.builder().build();
        return interviewRepository.save(interview).getId();
    }
}
