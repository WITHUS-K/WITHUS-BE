package KUSITMS.WITHUS.domain.interview.timeslot.service.util.candidate;

import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity.InterviewerAvailability;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CandidateIndex {
    private final Map<Long, Set<LocalDateTime>> availableTimes;
    private final Map<Long, User> userMap;

    /**
     * 가용시간 인덱스 구성
     */
    public static CandidateIndex of(final List<InterviewerAvailability> availabilities,
                                    final UserRepository userRepository) {

        final Map<Long, Set<LocalDateTime>> times = availabilities.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getUser().getId(),
                        Collectors.mapping(
                                InterviewerAvailability::getAvailableTime,
                                Collectors.toCollection(HashSet::new)
                        )
                ));

        final Map<Long, User> users = userRepository.findAllById(times.keySet().stream().toList())
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return new CandidateIndex(times, users);
    }
}
