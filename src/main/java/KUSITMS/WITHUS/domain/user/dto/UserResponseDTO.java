package KUSITMS.WITHUS.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserResponseDTO {

    @Schema(description = "이메일 중복 확인 응답 DTO")
    public record EmailDuplicateCheck (
            @Schema(description = "이메일 중복 여부", example = "false") boolean isDuplicated
    ) {}
}
