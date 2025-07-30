package KUSITMS.WITHUS.domain.application.comment.repository;

import KUSITMS.WITHUS.domain.application.comment.entity.Comment;
import KUSITMS.WITHUS.domain.application.comment.enumerate.CommentType;

import java.util.List;

public interface CommentRepository {
    Comment save(Comment comment);
    List<Comment> findByApplicationIdAndTypeAndUser(Long applicationId, CommentType type, Long userId);
    List<Comment> findByApplicationIdAndType(Long id, CommentType commentType);
    Comment getById(Long commentId);
    void delete(Comment comment);
}
