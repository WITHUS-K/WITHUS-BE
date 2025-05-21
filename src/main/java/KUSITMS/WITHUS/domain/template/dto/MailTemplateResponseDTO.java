package KUSITMS.WITHUS.domain.template.dto;

import KUSITMS.WITHUS.domain.template.entity.MailTemplate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메일 템플릿 응답 DTO")
public class MailTemplateResponseDTO {

    @Schema(description = "메일 템플릿 상세 응답 DTO")
    public record Detail(
            Long id,
            String name,
            String subject,
            String body
    ) {
        public static Detail from(MailTemplate e) {
            return new Detail(
                    e.getId(),
                    e.getName(),
                    e.getSubject(),
                    e.getBody()
            );
        }
    }

    @Schema(description = "메일 템플릿 요약 응답 DTO")
    public record Summary(
            Long id,
            String name
    ) {
        public static Summary from(MailTemplate e) {
            return new Summary(
                    e.getId(),
                    e.getName()
            );
        }
    }
}
