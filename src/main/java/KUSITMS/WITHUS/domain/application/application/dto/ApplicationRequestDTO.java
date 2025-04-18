package KUSITMS.WITHUS.domain.application.application.dto;

import KUSITMS.WITHUS.global.common.enumerate.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "지원서 요청 DTO")
public class ApplicationRequestDTO {

    @Schema(description = "지원서 생성 요청 DTO")
    public record Create(
            @Schema(description = "지원자 이름", example = "김김김")
            @NotBlank String name,

            @Schema(description = "성별", example = "MALE")
            @NotNull Gender gender,

            @Schema(description = "이메일", example = "test@example.com")
            @NotBlank @Email String email,

            @Schema(description = "전화번호", example = "01012341234")
            @NotBlank String phoneNumber,

            @Schema(description = "대학명", example = "상명대학교")
            String university,

            @Schema(description = "전공명", example = "컴퓨터공학과")
            String major,

            @Schema(description = "생년월일", example = "2000-01-01")
            LocalDate birthDate,

            @Schema(description = "이미지 URL", example = "https://cdn.withus.com/image.jpg")
            String imageUrl,

            @Schema(description = "양식 ID", example = "1")
            @NotNull Long templateId,

            @Schema(description = "파트 ID", example = "1")
            @NotNull Long positionId
    ) {}
}
