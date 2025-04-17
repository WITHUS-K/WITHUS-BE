package KUSITMS.WITHUS.domain.recruitment.dto;

import KUSITMS.WITHUS.domain.recruitment.entity.Recruitment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "리크루팅(공고) 관련 응답 DTO")
public class RecruitmentResponseDTO {

    @Schema(description = "공고 생성 응답 DTO")
    public record Create(
            @Schema(description = "공고 Id") Long recruitmentId
    ) {
        public static Create from(Recruitment recruitment) {
            return new Create(
                    recruitment.getId()
            );
        }
    }

    @Schema(description = "공고 상세 정보 응답 DTO")
    public record Detail(
            @Schema(description = "공고 Id") Long recruitmentId,
            @Schema(description = "공고 제목") String title,
            @Schema(description = "공고 내용") String content,
            @Schema(description = "첨부 파일 URL") String fileUrl,
            @Schema(description = "서류 마감일") LocalDate documentDeadline,
            @Schema(description = "서류 발표일") LocalDate documentResultDate,
            @Schema(description = "최종 발표일") LocalDate finalResultDate,
            @Schema(description = "조직명") String organizationName
    ) {
        public static Detail from(Recruitment recruitment) {
            return new Detail(
                    recruitment.getId(),
                    recruitment.getTitle(),
                    recruitment.getContent(),
                    recruitment.getFileUrl(),
                    recruitment.getDocumentDeadline(),
                    recruitment.getDocumentResultDate(),
                    recruitment.getFinalResultDate(),
                    recruitment.getOrganization().getName()
            );
        }
    }

    @Schema(description = "공고 수정 응답 DTO")
    public record Update(
            @Schema(description = "공고 Id") Long recruitmentId
    ) {
        public static Update from(Recruitment recruitment) {
            return new Update(
                    recruitment.getId()
            );
        }
    }

    @Schema(description = "공고 요약 정보 응답 DTO")
    public record Summary(
            @Schema(description = "공고 Id") Long recruitmentId,
            @Schema(description = "공고 제목") String title,
            @Schema(description = "서류 마감일") LocalDate documentDeadline,
            @Schema(description = "서류 발표일") LocalDate documentResultDate,
            @Schema(description = "최종 발표일") LocalDate finalResultDate,
            @Schema(description = "조직명") String organizationName
    ) {
        public static Summary from(Recruitment recruitment) {
            return new Summary(
                    recruitment.getId(),
                    recruitment.getTitle(),
                    recruitment.getDocumentDeadline(),
                    recruitment.getDocumentResultDate(),
                    recruitment.getFinalResultDate(),
                    recruitment.getOrganization().getName()
            );
        }
    }
}
