package KUSITMS.WITHUS.domain.user.controller;

import KUSITMS.WITHUS.domain.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.service.UserService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원 Controller")
@RequestMapping("api/v1/auth")
public class UserController {

    private final UserService joinService;

    @PostMapping("/join")
    @Operation(summary = "회원가입 API")
    public SuccessResponse<String> joinProcess(UserRequestDTO.Join request) {

        joinService.joinProcess(request);

        return SuccessResponse.ok("회원가입이 완료되었습니다.");
    }
}