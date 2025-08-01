package KUSITMS.WITHUS.domain.interview.timeslot.dto;

import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;

import java.time.LocalDate;
import java.time.LocalTime;

public record SimSlot(
        LocalDate date,
        LocalTime start,
        LocalTime end,
        Position position,
        String roomName
) {}
