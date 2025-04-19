package KUSITMS.WITHUS.domain.application.template.dto;

import KUSITMS.WITHUS.domain.application.template.entity.ApplicationTemplate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "지원서 양식 관련 응답 DTO")
public class ApplicationTemplateResponseDTO {

    @Schema(description = "지원서 양식 상세 조회 응답 DTO")
    public record Detail(
            @Schema(description = "양식 ID") Long id,
            @Schema(description = "양식 제목") String title
    ) {
        public static Detail from(ApplicationTemplate template) {
            return new Detail(
                    template.getId(),
                    template.getTitle()
            );
        }
    }
}
