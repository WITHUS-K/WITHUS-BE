package KUSITMS.WITHUS.domain.interview.interview.service;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewResponseDTO;
import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewScheduleDTO;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.interview.repository.InterviewRepository;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final RecruitmentRepository recruitmentRepository;

    @Override
    @Transactional
    public Long create(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.getById(recruitmentId);

        Interview interview = Interview.builder()
                .recruitment(recruitment)
                .build();

        List<Application> applications = applicationRepository.findPassedByRecruitment(recruitmentId);

        for (Application app : applications) {
            app.associateInterview(interview);
        }

        return interviewRepository.save(interview).getId();
    }

    @Override
    public Interview getById(Long interviewId) {
        return interviewRepository.getById(interviewId);
    }

    @Override
    public List<InterviewScheduleDTO.InterviewScheduleSummaryDTO> getOrganizationInterviews(Long organizationId) {
        // 해당 조직의 모든 공고 조회
        List<Recruitment> recruitments = recruitmentRepository.findAllByOrganizationId(organizationId);
        if (recruitments.isEmpty()) {
            return Collections.emptyList();
        }

        // 공고 ID 리스트 추출
        List<Long> recruitmentIds = recruitments.stream()
                .map(Recruitment::getId)
                .toList();

        // 해당 공고들에 매핑된 Interview 조회
        List<Interview> interviews = interviewRepository.findAllByRecruitmentIdIn(recruitmentIds);
        Map<Long, Interview> interviewByRecruitmentId = interviews.stream()
                .collect(Collectors.toMap(
                        i -> i.getRecruitment().getId(),
                        Function.identity()
                ));

        // DTO 변환
        return recruitments.stream()
                .filter(r -> interviewByRecruitmentId.containsKey(r.getId()))
                .map(r -> {
                    Interview interview = interviewByRecruitmentId.get(r.getId());

                    List<AvailableTimeRangeResponseDTO> timeRanges = r.getAvailableTimeRanges().stream()
                            .map(AvailableTimeRangeResponseDTO::from)
                            .toList();

                    return new InterviewScheduleDTO.InterviewScheduleSummaryDTO(
                            r.getId(),
                            r.getTitle(),
                            interview.getId(),
                            timeRanges,
                            r.getInterviewDuration()
                    );
                })
                .toList();
    }

    @Override
    public InterviewResponseDTO.Config getInterviewConfig(Long interviewId) {
        Interview interview = interviewRepository.getById(interviewId);

        return InterviewResponseDTO.Config.from(
                interview,
                interview.getInterviewerPerSlot(),
                interview.getApplicantPerSlot(),
                interview.getAssistantPerSlot()
        );
    }
}
