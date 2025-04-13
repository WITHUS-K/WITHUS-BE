package KUSITMS.WITHUS.global.auth.dto;

import KUSITMS.WITHUS.global.auth.enumerate.Role;

public record JoinDTO (
    String email,
    String password,
    Role role
) {}
