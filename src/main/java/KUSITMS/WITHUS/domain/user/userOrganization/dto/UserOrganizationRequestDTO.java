package KUSITMS.WITHUS.domain.user.userOrganization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "조직 사용자 관련 요청 DTO")
public class UserOrganizationRequestDTO {

    @Schema(description = "조직에 사용자 추가 요청 DTO")
    public record AddUser(
            @Schema(description = "추가할 사용자 ID", example = "1")
            @NotNull Long userId
    ) {}
}
