package KUSITMS.WITHUS.domain.application.entity;

import KUSITMS.WITHUS.domain.application.template.entity.ApplicationTemplate;
import KUSITMS.WITHUS.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "APPLICATION_QUESTION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_ID", nullable = false)
    private ApplicationTemplate template;

    @Column(nullable = false)
    private String Content;

    public void associateTemplate(ApplicationTemplate template) {
        this.template = template;
    }

}
