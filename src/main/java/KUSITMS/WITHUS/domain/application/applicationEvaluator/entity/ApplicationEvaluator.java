package KUSITMS.WITHUS.domain.application.applicationEvaluator.entity;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "APPLICATION_EVALUATOR")
@Getter
@NoArgsConstructor
public class ApplicationEvaluator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPLICATION_EVALUATOR_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICATION_ID", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVALUATOR_ID", nullable = false)
    private User evaluator;

    public ApplicationEvaluator(Application application, User evaluator) {
        this.application = application;
        this.evaluator   = evaluator;
    }
}

