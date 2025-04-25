package KUSITMS.WITHUS.domain.application.comment.entity;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.comment.enumerate.CommentType;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "COMMENT")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "COMMENT_TYPE", nullable = false)
    private CommentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICATION_ID", nullable = false)
    private Application application;

    public void associateUser(User user) {
        this.user = user;
    }

    public void associateApplication(Application application) {
        this.application = application;
    }
}
