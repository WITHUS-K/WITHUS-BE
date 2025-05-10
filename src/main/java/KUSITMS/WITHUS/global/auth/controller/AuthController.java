package KUSITMS.WITHUS.global.auth.controller;

import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.global.auth.service.AuthService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "인증 Controller")
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "이메일과 비밀번호로 로그인 후 JWT 토큰을 헤더에 담아 반환합니다.")
    public ResponseEntity<Void> login(
            @RequestBody UserRequestDTO.Login request
    ) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue")
    @Operation(summary = "Access Token 재발급", description = "Refresh Token을 이용해 Access Token을 재발급합니다.")
    public ResponseEntity<Void> reissue(
            @RequestHeader("Refresh-Token") String refreshToken
    ) {
        String newAccessToken = authService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + newAccessToken)
                .build();
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자의 Refresh Token을 삭제하여 로그아웃합니다.")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/verify")
    @Operation(summary = "이메일 인증번호 요청")
    public SuccessResponse<String> requestEmailVerification(@RequestBody UserRequestDTO.EmailRequest request) {
        authService.requestEmailVerification(request.name(), request.email());
        return SuccessResponse.ok("인증번호가 발송되었습니다.");
    }

    @PostMapping("/email/confirm")
    @Operation(summary = "이메일 인증번호 요청")
    public SuccessResponse<String> confirmEmailVerification(@RequestBody UserRequestDTO.EmailConfirmRequest request) {
        authService.confirmVerification(request.email(), request.code());
        return SuccessResponse.ok("이메일 인증이 완료되었습니다.");
    }

    @PostMapping("/phone/verify")
    @Operation(summary = "휴대폰 인증번호 요청")
    public SuccessResponse<String> requestPhoneVerification(@RequestBody @Valid UserRequestDTO.PhoneRequest request) {
        authService.requestPhoneVerification(request.phoneNumber());
        return SuccessResponse.ok("인증번호가 발송되었습니다.");
    }

    @PostMapping("/phone/confirm")
    @Operation(summary = "휴대폰 인증번호 확인")
    public SuccessResponse<String> confirmPhoneVerification(@RequestBody @Valid UserRequestDTO.PhoneConfirmRequest request) {
        authService.confirmVerification(request.phoneNumber(), request.code());
        return SuccessResponse.ok("휴대폰 인증이 완료되었습니다.");
    }
}
