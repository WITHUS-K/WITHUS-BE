package KUSITMS.WITHUS.domain.application.comment.service;

import KUSITMS.WITHUS.domain.application.comment.dto.CommentRequestDTO;
import KUSITMS.WITHUS.domain.application.comment.dto.CommentResponseDTO;

public interface CommentService {
    CommentResponseDTO.Detail addComment(Long applicationId, Long userId, CommentRequestDTO.Create request);
    CommentResponseDTO.Summary updateComment(Long commentId, Long userId, CommentRequestDTO.Update request);
    void deleteComment(Long commentId, Long userId);
}
