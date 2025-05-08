package KUSITMS.WITHUS.domain.interview.interview.service;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.interview.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional
    public Long create(Long recruitmentId) {
        Interview interview = Interview.builder().build();

        List<Application> applications = applicationRepository.findPassedByRecruitment(recruitmentId);

        for (Application app : applications) {
            app.associateInterview(interview);
        }

        return interviewRepository.save(interview).getId();
    }
}
