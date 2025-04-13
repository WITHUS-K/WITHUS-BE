package KUSITMS.WITHUS.domain.user.controller;

import KUSITMS.WITHUS.domain.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.service.JoinService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class JoinController {

    private final JoinService joinService;

    @PostMapping("/join")
    public SuccessResponse<String> joinProcess(UserRequestDTO.Join request) {

        joinService.joinProcess(request);

        return SuccessResponse.ok("회원가입이 완료되었습니다.");
    }
}