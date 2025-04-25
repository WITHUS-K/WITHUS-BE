package KUSITMS.WITHUS.domain.application.comment.repository;

import KUSITMS.WITHUS.domain.application.comment.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;

    @Override
    public Comment save(Comment comment) {
        return commentJpaRepository.save(comment);
    }
}
