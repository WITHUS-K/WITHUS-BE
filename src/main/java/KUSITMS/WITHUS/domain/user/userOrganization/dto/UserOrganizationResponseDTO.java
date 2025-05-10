package KUSITMS.WITHUS.domain.user.userOrganization.dto;

import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "조직 사용자 관련 응답 DTO")
public class UserOrganizationResponseDTO {

    @Schema(description = "조직에 사용자 추가 응답 DTO")
    public record Detail(
            @Schema(description = "UserOrganization ID") Long id,
            @Schema(description = "사용자 이름") String userName,
            @Schema(description = "조직 이름") String organizationName
    ) {
        public static Detail from(UserOrganization userOrganization) {
            return new Detail(
                    userOrganization.getId(),
                    userOrganization.getUser().getName(),
                    userOrganization.getOrganization().getName()
            );
        }
    }
}
