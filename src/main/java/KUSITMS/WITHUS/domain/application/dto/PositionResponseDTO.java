package KUSITMS.WITHUS.domain.application.dto;

import KUSITMS.WITHUS.domain.application.entity.Position;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파트 관련 응답 DTO")
public class PositionResponseDTO {

    @Schema(description = "파트 상세 조회 응답 DTO")
    public record Detail(
            @Schema(description = "파트 ID") Long id,
            @Schema(description = "파트 이름") String name
    ) {
        public static Detail from(Position position) {
            return new Detail(
                    position.getId(),
                    position.getName()
            );
        }
    }
}
