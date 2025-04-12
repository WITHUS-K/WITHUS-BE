package KUSITMS.WITHUS.domain.auth.dto;

import KUSITMS.WITHUS.domain.auth.enumerate.Role;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinDTO {

    private String username;
    private String password;
    private Role role;
}
