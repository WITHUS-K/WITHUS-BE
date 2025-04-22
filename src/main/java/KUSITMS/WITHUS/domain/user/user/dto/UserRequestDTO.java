package KUSITMS.WITHUS.domain.user.user.dto;

import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 관련 요청 DTO")
public class UserRequestDTO {

    @Schema(description = "회원가입 요청 DTO")
    public record Join (
            @Schema(description = "이메일", example = "test@gmail.com") String email,
            @Schema(description = "비밀번호", example = "1234") String password,
            @Schema(description = "권한", example = "USER") Role role,
            @Schema(description = "이름", example = "홍길동") String name,
            @Schema(description = "성별", example = "MALE") Gender gender,
            @Schema(description = "전화번호", example = "01012345678") String phoneNumber
    ) {}
}
