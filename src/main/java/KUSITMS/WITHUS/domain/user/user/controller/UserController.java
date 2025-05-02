package KUSITMS.WITHUS.domain.user.user.controller;

import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.user.service.UserService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "회원 Controller")
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/join/admin")
    @Operation(summary = "관리자 회원가입 API")
    public SuccessResponse<String> adminJoinProcess(
            @RequestBody @Valid UserRequestDTO.AdminJoin request) {

        userService.adminJoinProcess(request);

        return SuccessResponse.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/join/user")
    @Operation(summary = "사용자 회원가입 API")
    public SuccessResponse<String> userJoinProcess(
            @RequestBody @Valid UserRequestDTO.UserJoin request) {

        userService.userJoinProcess(request);

        return SuccessResponse.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "비밀번호 재설정 API")
    public SuccessResponse<String> resetPassword(@RequestBody @Valid UserRequestDTO.ResetPassword request) {
        userService.resetPassword(request.email(), request.newPassword());
        return SuccessResponse.ok("비밀번호 재설정이 완료되었습니다.");
    }

    @GetMapping("/email/check")
    @Operation(summary = "이메일 중복 확인 API")
    public SuccessResponse<UserResponseDTO.EmailDuplicateCheck> checkEmailDuplicate(
            @RequestParam("email") @Email String email
    ) {
        UserResponseDTO.EmailDuplicateCheck response = new UserResponseDTO.EmailDuplicateCheck(userService.isEmailDuplicated(email));
        return SuccessResponse.ok(response);
    }

}