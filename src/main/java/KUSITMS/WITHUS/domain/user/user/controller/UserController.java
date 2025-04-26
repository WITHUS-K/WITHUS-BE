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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "회원 Controller")
@RequestMapping("api/v1/auth")
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

    @GetMapping("/email/check")
    @Operation(summary = "이메일 중복 확인 API")
    public SuccessResponse<UserResponseDTO.EmailDuplicateCheck> checkEmailDuplicate(
            @RequestParam("email") @Email String email
    ) {
        UserResponseDTO.EmailDuplicateCheck response = new UserResponseDTO.EmailDuplicateCheck(userService.isEmailDuplicated(email));
        return SuccessResponse.ok(response);
    }

    @PostMapping("/phone/verify")
    @Operation(summary = "휴대폰 인증번호 요청")
    public SuccessResponse<String> requestPhoneVerification(@RequestBody @Valid UserRequestDTO.PhoneRequest request) {
        userService.requestPhoneVerification(request.phoneNumber());
        return SuccessResponse.ok("인증번호가 발송되었습니다.");
    }

    @PostMapping("/phone/confirm")
    @Operation(summary = "휴대폰 인증번호 확인")
    public SuccessResponse<String> confirmPhoneVerification(@RequestBody @Valid UserRequestDTO.PhoneConfirmRequest request) {
        userService.confirmPhoneVerification(request.phoneNumber(), request.code());
        return SuccessResponse.ok("휴대폰 인증이 완료되었습니다.");
    }
}