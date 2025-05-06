package KUSITMS.WITHUS.domain.application.comment.repository;

import KUSITMS.WITHUS.domain.application.comment.entity.Comment;
import KUSITMS.WITHUS.domain.application.comment.enumerate.CommentType;

import java.util.List;

public interface CommentRepository {
    Comment save(Comment comment);
    List<Comment> findByApplicationIdAndTypeAndCreatedBy(Long applicationId, CommentType type, Long userId);
}
