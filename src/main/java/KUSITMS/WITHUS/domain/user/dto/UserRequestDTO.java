package KUSITMS.WITHUS.domain.user.dto;

import KUSITMS.WITHUS.domain.user.enumerate.Role;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Schema(description = "회원 관련 요청 DTO")
public class UserRequestDTO {

    @Schema(description = "회원가입 요청 DTO")
    public record Join (
            @Schema(description = "이메일", example = "test@gmail.com") String email,
            @Schema(description = "비밀번호", example = "1234") String password,
            @Schema(description = "권한", example = "USER") Role role
    ) {}

    @Schema(description = "관리자 회원가입 요청 DTO")
    public record AdminJoin(
            @Schema(description = "이름", example = "홍길동")
            @NotBlank String name,

            @Schema(description = "동아리명", example = "큐시즘")
            @NotBlank String organizationName,

            @Schema(description = "이메일", example = "admin@example.com")
            @Email @NotBlank String email,

            @Schema(description = "비밀번호", example = "Abc123!@")
            @NotBlank
            @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 조합하여 8~20자로 입력해야 합니다."
            )
            String password,

            @Schema(description = "휴대폰 번호", example = "01012345678")
            @NotBlank String phoneNumber
    ) {}

    @Schema(description = "사용자 회원가입 요청 DTO")
    public record UserJoin(
            @Schema(description = "이름", example = "홍길동")
            @NotBlank String name,

            @Schema(description = "생년월일", example = "2000-01-01")
            @NotNull LocalDate birthDate,

            @Schema(description = "성별", example = "MALE | FEMALE | NONE")
            @NotNull Gender gender,

            @Schema(description = "동아리 Id", example = "큐시즘")
            @NotNull Long organizationId,

            @Schema(description = "이메일", example = "admin@example.com")
            @Email @NotBlank String email,

            @Schema(description = "비밀번호", example = "Abc123!@")
            @NotBlank
            @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 조합하여 8~20자로 입력해야 합니다."
            )
            String password,

            @Schema(description = "휴대폰 번호", example = "01012345678")
            @NotBlank String phoneNumber
    ) {}

    @Schema(description = "인증 번호 요청 DTO")
    public record PhoneRequest(
            @Schema(description = "휴대폰 번호", example = "01012345678")
            @NotBlank
            String phoneNumber
    ) {}

    @Schema(description = "인증 번호 확인 DTO")
    public record PhoneConfirmRequest(
            @Schema(description = "휴대폰 번호", example = "01012345678")
            @NotBlank
            String phoneNumber,

            @Schema(description = "인증번호", example = "487593")
            @NotBlank
            String code
    ) {}
}
