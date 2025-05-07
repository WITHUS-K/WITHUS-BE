package KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper;

import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.position.repository.PositionRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PositionAppender {

    private final PositionRepository positionRepository;

    public void append(Recruitment recruitment, List<String> positionNames) {
        if (positionNames == null) return;

        positionNames.stream()
                .distinct()
                .forEach(name -> {
                    Position existing = positionRepository.findByRecruitmentAndName(recruitment, name)
                            .orElseGet(() -> {
                                Position p = Position.builder()
                                        .name(name)
                                        .recruitment(recruitment)
                                        .build();
                                return positionRepository.save(p);
                            });

                    recruitment.addPosition(existing);
                });
    }
}
