package KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.service;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.interview.repository.InterviewRepository;
import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity.InterviewerAvailability;
import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.repository.InterviewerAvailabilityRepository;
import KUSITMS.WITHUS.domain.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewerAvailabilityServiceImpl implements InterviewerAvailabilityService {

    private final InterviewerAvailabilityRepository availabilityRepository;
    private final InterviewRepository interviewRepository;

    /**
     * 사용자(운영진)이 면접 가능한 시간을 등록
     * @param interviewId 해당 면접 ID
     * @param user 사용자 ID (현재 로그인 유저)
     * @param times 가능한 시간
     * @return 저장된 시간의 정보
     */
    @Override
    @Transactional
    public List<InterviewerAvailability> registerAvailability(Long interviewId, User user, List<LocalDateTime> times) {
        Interview interview = interviewRepository.getById(interviewId);

        Recruitment recruitment = interview.getApplications().stream()
                .findFirst()
                .map(Application::getRecruitment)
                .orElseThrow(() -> new CustomException(ErrorCode.RECRUITMENT_NOT_EXIST));

        boolean isManager = recruitment.getOrganization().getUserOrganizations().stream()
                .anyMatch(uo -> uo.getUser().getId().equals(user.getId()));

        if (!isManager) {
            throw new CustomException(ErrorCode.USER_NO_PERMISSION);
        }

        List<InterviewerAvailability> availabilities = times.stream()
                .map(time -> {
                    InterviewerAvailability availability = InterviewerAvailability.of(interview, user, time);
                    interview.addInterviewerAvailability(availability);
                    return availability;
                })
                .toList();

        return availabilityRepository.saveAll(availabilities);
    }
}
