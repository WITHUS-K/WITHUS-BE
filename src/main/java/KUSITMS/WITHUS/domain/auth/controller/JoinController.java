package KUSITMS.WITHUS.domain.auth.controller;

import KUSITMS.WITHUS.domain.auth.dto.JoinDTO;
import KUSITMS.WITHUS.domain.auth.service.JoinService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {

        this.joinService = joinService;
    }

    @PostMapping("/join")
    public SuccessResponse<String> joinProcess(JoinDTO joinDTO) {

        System.out.println(joinDTO.getUsername());
        joinService.joinProcess(joinDTO);

        return SuccessResponse.ok("회원가입이 완료되었습니다.");
    }
}