package KUSITMS.WITHUS.domain.recruitment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "리크루팅(공고) 관련 요청 DTO")
public class RecruitmentRequestDTO {

    @Schema(description = "공고 생성 요청 DTO")
    public record Create (
            @Schema(description = "공고 제목", example = "2025-1 큐시즘 모집")
            @NotBlank String title,

            @Schema(description = "공고 내용", example = "큐시즘 학회원 모집합니다.")
            @NotBlank String content,

            @Schema(description = "첨부 파일 URL", example = "https://withus.com/files/recruit.pdf")
            String fileUrl,

            @Schema(description = "서류 마감일", example = "2025-05-01")
            @NotNull @Future LocalDate documentDeadline,

            @Schema(description = "서류 발표일", example = "2025-05-10")
            @NotNull @Future LocalDate documentResultDate,

            @Schema(description = "최종 발표일", example = "2025-05-20")
            @NotNull @Future LocalDate finalResultDate,

            @Schema(description = "조직 ID", example = "1")
            @NotNull Long organizationId
    ) {}

    @Schema(description = "공고 수정 요청 DTO")
    public record Update (
            @Schema(description = "공고 제목", example = "2025-2 큐시즘 모집")
            @NotBlank String title,

            @Schema(description = "공고 내용", example = "큐시즘 학회원 추가 모집합니다.")
            @NotBlank String content,

            @Schema(description = "첨부 파일 URL", example = "https://withus.com/files/recruit_v2.pdf")
            String fileUrl,

            @Schema(description = "서류 마감일", example = "2025-06-01")
            @NotNull @Future LocalDate documentDeadline,

            @Schema(description = "서류 발표일", example = "2025-06-10")
            @NotNull @Future LocalDate documentResultDate,

            @Schema(description = "최종 발표일", example = "2025-06-20")
            @NotNull @Future LocalDate finalResultDate
    ) {}

}
