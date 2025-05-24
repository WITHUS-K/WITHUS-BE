package KUSITMS.WITHUS.global.infra.upload.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "파일 요청 DTO")
public class FileRequestDTO {

    @Schema(description = "파일 다운로드 요청 DTO")
    public record Download(
            @Schema(description = "다운로드할 원격 파일 URL", example = "https://.../SAP.pdf")
            @NotBlank
            String imageUrl,

            @Schema(description = "클라이언트에 제시할 파일명", example = "SAP.pdf")
            @NotBlank
            String fileName
    ) {}
}
