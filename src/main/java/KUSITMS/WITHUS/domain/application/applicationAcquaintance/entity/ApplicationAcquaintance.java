package KUSITMS.WITHUS.domain.application.applicationAcquaintance.entity;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "APPLICATION_ACQUAINTANCE")
@Getter
@NoArgsConstructor
public class ApplicationAcquaintance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACQUAINTANCE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICATION_ID", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    public ApplicationAcquaintance(Application application, User user) {
        this.application = application;
        this.user        = user;
    }
}

