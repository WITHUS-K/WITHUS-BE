package KUSITMS.WITHUS.domain.application.comment.dto;

import KUSITMS.WITHUS.domain.application.comment.entity.Comment;
import KUSITMS.WITHUS.domain.application.comment.enumerate.CommentType;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "코맨트 관련 응답 DTO")
public class CommentResponseDTO {

    @Schema(description = "코맨트 요약 정보 응답 DTO")
    public record Summary(
            @Schema(description = "코맨트 내용") String content,
            @Schema(description = "코멘트 유형") CommentType type,
            @Schema(description = "작성자") String userName
    ) {
        public static Summary from(Comment comment) {
            return new Summary(
                    comment.getContent(),
                    comment.getType(),
                    comment.getUser().getName()
            );
        }
    }

    @Schema(description = "코맨트 상세 조회 응답 DTO")
    public record Detail(
            @Schema(description = "코맨트 ID") Long id,
            @Schema(description = "코맨트 내용") String content,
            @Schema(description = "코멘트 유형") CommentType type,
            @Schema(description = "작성자") UserResponseDTO.Summary user
    ) {
        public static Detail from(Comment comment) {
            return new Detail(
                    comment.getId(),
                    comment.getContent(),
                    comment.getType(),
                    UserResponseDTO.Summary.from(comment.getUser())
            );
        }
    }
}
