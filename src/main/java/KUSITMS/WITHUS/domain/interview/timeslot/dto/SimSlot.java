package KUSITMS.WITHUS.domain.interview.timeslot.dto;

import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;

import java.time.LocalDate;
import java.time.LocalTime;

public record SimSlot(
        LocalDate date,
        LocalTime start,
        LocalTime end,
        OrganizationRole organizationRole,
        String roomName
) {}
