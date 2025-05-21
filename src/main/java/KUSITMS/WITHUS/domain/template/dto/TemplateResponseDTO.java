package KUSITMS.WITHUS.domain.template.dto;

import KUSITMS.WITHUS.domain.template.entity.Template;
import KUSITMS.WITHUS.domain.template.enumerate.Medium;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메일 템플릿 응답 DTO")
public class TemplateResponseDTO {

    @Schema(description = "템플릿 상세 응답 DTO")
    public record Detail(
            Long id,
            String name,
            String subject,
            String body,
            Medium medium
    ) {
        public static Detail from(Template e) {
            return new Detail(
                    e.getId(),
                    e.getName(),
                    e.getSubject(),
                    e.getBody(),
                    e.getMedium()
            );
        }
    }

    @Schema(description = "템플릿 요약 응답 DTO")
    public record Summary(
            Long id,
            String name,
            Medium medium
    ) {
        public static Summary from(Template e) {
            return new Summary(
                    e.getId(),
                    e.getName(),
                    e.getMedium()
            );
        }
    }
}
