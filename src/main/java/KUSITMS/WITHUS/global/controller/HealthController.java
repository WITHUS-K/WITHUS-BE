package KUSITMS.WITHUS.global.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "서버 상태 체크를 위한 API")
public class HealthController {
    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}
