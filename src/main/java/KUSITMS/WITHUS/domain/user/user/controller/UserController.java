package KUSITMS.WITHUS.domain.user.user.controller;

import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.service.UserService;
import KUSITMS.WITHUS.global.common.annotation.CurrentUser;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/email")
    @Operation(summary = "이메일로 사용자 단건 조회 API", description = "이메일이 정확히 일치하는 사용자를 조회합니다.")
    public SuccessResponse<UserResponseDTO.SummaryForSearch> getUserByEmail(
            @RequestParam("email") @Email String email
    ) {
        User user = userService.getUserByEmail(email);
        return SuccessResponse.ok(UserResponseDTO.SummaryForSearch.from(user));
    }

    @GetMapping("/my-page")
    @Operation(summary = "마이페이지 조회")
    public SuccessResponse<UserResponseDTO.MyPage> getMyPage(@CurrentUser User user) {
        UserResponseDTO.MyPage response = userService.getMyPage(user.getId());
        return SuccessResponse.ok(response);
    }

    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원 정보 수정", description = "이름, 전화번호, 비밀번호, 프로필 이미지를 수정합니다.")
    public SuccessResponse<UserResponseDTO.MyPage> updateUser(
            @RequestPart(value = "request") @Valid UserRequestDTO.Update request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @CurrentUser User user
    ) {
        UserResponseDTO.MyPage response = userService.updateUser(request, profileImage, user);
        return SuccessResponse.ok(response);
    }
}