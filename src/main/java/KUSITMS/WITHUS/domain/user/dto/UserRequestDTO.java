package KUSITMS.WITHUS.domain.user.dto;

import KUSITMS.WITHUS.global.auth.enumerate.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 관련 요청 DTO")
public class UserRequestDTO {

    @Schema(description = "회원가입 요청 DTO")
    public record Join (
            @Schema(description = "이메일", example = "test@gmail.com") String email,
            @Schema(description = "비밀번호", example = "1234") String password,
            @Schema(description = "권한", example = "ROLE_USER") Role role
    ) {}
}
