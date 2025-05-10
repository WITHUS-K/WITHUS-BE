package KUSITMS.WITHUS.domain.application.comment.repository;

import KUSITMS.WITHUS.domain.application.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
}
