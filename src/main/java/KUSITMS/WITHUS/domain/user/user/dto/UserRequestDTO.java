package KUSITMS.WITHUS.domain.user.user.dto;

import KUSITMS.WITHUS.global.common.enumerate.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Schema(description = "회원 관련 요청 DTO")
public class UserRequestDTO {

    @Schema(description = "로그인 요청 DTO")
    public record Login (
            @Schema(description = "이메일", example = "user@example.com") @NotBlank String email,
            @Schema(description = "비밀번호", example = "Abc123!@") @NotBlank String password
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

    @Schema(description = "비밀번호 재설정 요청 DTO")
    public record ResetPassword(
            @Schema(description = "이메일", example = "user@example.com")
            @NotBlank @Email String email,

            @Schema(description = "새로운 비밀번호", example = "Abc123!@")
            @NotBlank
            @Pattern(
                    regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
                    message = "비밀번호는 영문, 숫자, 특수문자를 조합하여 8~20자로 입력해야 합니다."
            )
            String newPassword
    ) {}

    @Schema(description = "이메일 인증 번호 요청 DTO")
    public record EmailRequest (
            @Schema(description = "이름", example = "홍길동")
            @NotBlank
            String name,

            @Schema(description = "이메일 주소", example = "user@example.com")
            @NotBlank
            String email
    ) {}

    @Schema(description = "이메일 인증 번호 확인 요청 DTO")
    public record EmailConfirmRequest (
            @Schema(description = "이메일", example = "user@example.com")
            @NotBlank @Email
            String email,

            @Schema(description = "인증번호", example = "487593")
            @NotBlank
            String code
    ) {}

    @Schema(description = "휴대폰 인증 번호 요청 DTO")
    public record PhoneRequest(
            @Schema(description = "휴대폰 번호", example = "01012345678")
            @NotBlank
            String phoneNumber
    ) {}

    @Schema(description = "휴대폰 인증 번호 확인 요청 DTO")
    public record PhoneConfirmRequest(
            @Schema(description = "휴대폰 번호", example = "01012345678")
            @NotBlank
            String phoneNumber,

            @Schema(description = "인증번호", example = "487593")
            @NotBlank
            String code
    ) {}

    @Schema(description = "회원 정보 수정 요청 DTO")
    public record Update(
            @Schema(description = "이름", example = "홍길동")
            @NotBlank
            String name,

            @Schema(description = "전화번호", example = "01099999999")
            @NotBlank
            String phoneNumber,

            @Schema(description = "현재 비밀번호", example = "currentPassword")
            String currentPassword,

            @Schema(description = "새 비밀번호", example = "newPassword")
            String newPassword1,

            @Schema(description = "새 비밀번호 확인", example = "newPassword")
            String newPassword2
    ) {}
}
