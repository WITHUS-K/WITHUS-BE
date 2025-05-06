package KUSITMS.WITHUS.domain.user.userOrganization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "조직 사용자 관련 요청 DTO")
public class UserOrganizationRequestDTO {

    @Schema(description = "조직에 사용자 추가 요청 DTO")
    public record AddUsers(
            @Schema(description = "추가할 사용자 ID 목록", example = "[1, 2]")
            @NotEmpty List<Long> userIds
    ) {}

    @Schema(description = "조직 사용자 일괄 삭제 요청 DTO")
    public record DeleteUsers(
            @Schema(description = "삭제할 사용자 ID 목록", example = "[1, 2, 3]")
            @NotEmpty List<Long> userIds
    ) {}
}
