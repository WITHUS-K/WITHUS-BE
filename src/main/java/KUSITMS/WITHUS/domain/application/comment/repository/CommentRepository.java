package KUSITMS.WITHUS.domain.application.comment.repository;

import KUSITMS.WITHUS.domain.application.comment.entity.Comment;

public interface CommentRepository {
    Comment save(Comment comment);
}
