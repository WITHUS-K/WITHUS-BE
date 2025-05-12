package KUSITMS.WITHUS.domain.application.comment.repository;

import KUSITMS.WITHUS.domain.application.comment.entity.Comment;
import KUSITMS.WITHUS.domain.application.comment.enumerate.CommentType;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.application.comment.entity.QComment.comment;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Comment save(Comment comment) {
        return commentJpaRepository.save(comment);
    }

    @Override
    public List<Comment> findByApplicationIdAndTypeAndCreatedBy(Long applicationId, CommentType type, Long userId) {
        return queryFactory
                .selectFrom(comment)
                .where(
                        comment.application.id.eq(applicationId),
                        comment.type.eq(type),
                        comment.user.id.eq(userId)
                )
                .fetch();
    }

    @Override
    public Comment getById(Long commentId) {
        return commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_EXIST));
    }

    @Override
    public void delete(Comment comment) {
        commentJpaRepository.delete(comment);
    }

}
