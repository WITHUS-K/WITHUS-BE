package KUSITMS.WITHUS.domain.application.comment.service;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.application.comment.dto.CommentRequestDTO;
import KUSITMS.WITHUS.domain.application.comment.dto.CommentResponseDTO;
import KUSITMS.WITHUS.domain.application.comment.entity.Comment;
import KUSITMS.WITHUS.domain.application.comment.repository.CommentRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentResponseDTO.Summary addComment(Long applicationId, Long userId, CommentRequestDTO.Create request) {
        Application application = applicationRepository.getById(applicationId);
        User user = userRepository.getById(userId);

        Comment comment = Comment.builder()
                .content(request.content())
                .type(request.type())
                .application(application)
                .user(user)
                .build();

        application.addComment(comment);
        user.addComment(comment);

        return CommentResponseDTO.Summary.from(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentResponseDTO.Summary updateComment(Long commentId, Long userId, CommentRequestDTO.Update request) {
        Comment comment = commentRepository.getById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        comment.updateContent(request.content());
        return CommentResponseDTO.Summary.from(comment);
    }

}
