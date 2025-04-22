package KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.repository;

import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity.InterviewerAvailability;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InterviewerAvailabilityRepositoryImpl implements InterviewerAvailabilityRepository {

    private final InterviewerAvailabilityJpaRepository availabilityJpaRepository;

    @Override
    public List<InterviewerAvailability> saveAll(List<InterviewerAvailability> availabilities) {
        return availabilityJpaRepository.saveAll(availabilities);
    }
}
