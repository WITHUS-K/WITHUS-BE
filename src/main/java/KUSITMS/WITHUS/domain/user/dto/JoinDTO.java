package KUSITMS.WITHUS.domain.user.dto;

import KUSITMS.WITHUS.global.auth.enumerate.Role;

public record JoinDTO (
    String email,
    String password,
    Role role
) {}
