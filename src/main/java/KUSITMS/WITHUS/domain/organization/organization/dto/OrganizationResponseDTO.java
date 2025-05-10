package KUSITMS.WITHUS.domain.organization.organization.dto;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "조직(동아리) 관련 응답 DTO")
public class OrganizationResponseDTO {

    @Schema(description = "조직 생성 응답 DTO")
    public record Create(
            @Schema(description = "조직 ID") Long id,
            @Schema(description = "조직 이름") String name
    ) {
        public static Create from(Organization organization) {
            return new Create(
                    organization.getId(),
                    organization.getName()
            );
        }
    }

    //FIXME 아래 DTO들은 임시 생성. 이후 스프린트에서 수정 예정
    @Schema(description = "조직 정보 상세 조회 응답 DTO")
    public record Detail(
            @Schema(description = "조직 ID") Long id,
            @Schema(description = "조직 이름") String name
    ) {
        public static Detail from(Organization organization) {
            return new Detail(
                    organization.getId(),
                    organization.getName()
            );
        }
    }

    @Schema(description = "조직 수정 응답 DTO")
    public record Update(
            @Schema(description = "조직 ID") Long id,
            @Schema(description = "조직 이름") String name
    ) {
        public static Update from(Organization organization) {
            return new Update(
                    organization.getId(),
                    organization.getName()
            );
        }
    }

    @Schema(description = "조직 수정 응답 DTO")
    public record Summary(
            @Schema(description = "조직 ID") Long id,
            @Schema(description = "조직 이름") String name
    ) {
        public static Summary from(Organization organization) {
            return new Summary(
                    organization.getId(),
                    organization.getName()
            );
        }
    }

    @Schema(description = "조직 검색 응답 DTO")
    public record Search(
            Long id,
            String name
    ) {
        public static Search from(Organization organization) {
            return new Search(
                    organization.getId(),
                    organization.getName()
            );
        }
    }

}
