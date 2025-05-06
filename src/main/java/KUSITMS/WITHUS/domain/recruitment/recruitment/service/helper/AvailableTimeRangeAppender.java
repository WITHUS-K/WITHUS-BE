package KUSITMS.WITHUS.domain.recruitment.recruitment.service.helper;

import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.entity.AvailableTimeRange;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvailableTimeRangeAppender {

    public void append(Recruitment recruitment, List<AvailableTimeRangeRequestDTO> ranges) {
        if (ranges == null) return;

        ranges.stream()
                .map(dto -> AvailableTimeRange.builder()
                        .date(dto.date())
                        .startTime(dto.startTime())
                        .endTime(dto.endTime())
                        .recruitment(recruitment)
                        .build())
                .forEach(recruitment::addAvailableTimeRange);
    }
}

