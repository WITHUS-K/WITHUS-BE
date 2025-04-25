package KUSITMS.WITHUS.domain.user.controller;

import KUSITMS.WITHUS.domain.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.service.UserService;
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

    private final UserService joinService;

    @PostMapping("/join/admin")
    @Operation(summary = "관리자 회원가입 API")
    public SuccessResponse<String> adminJoinProcess(
            @RequestBody @Valid UserRequestDTO.AdminJoin request) {

        joinService.adminJoinProcess(request);

        return SuccessResponse.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/join/user")
    @Operation(summary = "사용자 회원가입 API")
    public SuccessResponse<String> userJoinProcess(
            @RequestBody @Valid UserRequestDTO.UserJoin request) {

        joinService.userJoinProcess(request);

        return SuccessResponse.ok("회원가입이 완료되었습니다.");
    }

    @GetMapping("/email/check")
    @Operation(summary = "이메일 중복 확인 API")
    public SuccessResponse<UserResponseDTO.EmailDuplicateCheck> checkEmailDuplicate(
            @RequestParam("email") @Email String email
    ) {
        UserResponseDTO.EmailDuplicateCheck response = new UserResponseDTO.EmailDuplicateCheck(joinService.isEmailDuplicated(email));
        return SuccessResponse.ok(response);
    }
}