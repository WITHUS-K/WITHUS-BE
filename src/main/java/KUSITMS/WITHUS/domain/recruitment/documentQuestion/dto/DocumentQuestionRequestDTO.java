package KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto;

import KUSITMS.WITHUS.domain.recruitment.documentQuestion.enumerate.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "서류 질문 관련 요청 DTO")
public class DocumentQuestionRequestDTO {

    @Schema(description = "서류 질문 생성 요청 DTO")
    public record Create(
            @Schema(description = "질문 제목", example = "자기소개를 해주세요")
            String title,

            @Schema(description = "질문 설명", example = "자유롭게 자신을 표현해 주세요.")
            String description,

            @Schema(description = "질문 형식(TEXT 또는 FILE)", example = "TEXT")
            @NotNull QuestionType type,

            @Schema(description = "필수 여부", example = "true") boolean required,

            // TEXT형 전용
            @Schema(description = "글자수 제한", example = "1000") Integer textLimit,
            @Schema(description = "공백 포함 여부", example = "true") Boolean includeWhitespace,

            // FILE형 전용
            @Schema(description = "최대 파일 수", example = "1") Integer maxFileCount,
            @Schema(description = "최대 파일 크기(MB)", example = "10") Integer maxFileSizeMb,

            @Schema(description = "적용할 파트 ID - null이면 공통", example = "백엔드") String positionName
    ) {}

}
