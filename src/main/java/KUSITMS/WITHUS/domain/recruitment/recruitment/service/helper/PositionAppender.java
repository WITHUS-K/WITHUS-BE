package KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper;

import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PositionAppender {

    public void append(Recruitment recruitment, List<String> positionNames) {
        if (positionNames == null) return;
        recruitment.clearPositions();

        Set<String> existing = recruitment.getPositions().stream()
                .map(Position::getName)
                .collect(Collectors.toSet());

        positionNames.stream()
                .filter(name -> !existing.contains(name))
                .map(name -> Position.builder()
                        .name(name)
                        .recruitment(recruitment)
                        .build())
                .forEach(recruitment::addPosition);
    }
}
