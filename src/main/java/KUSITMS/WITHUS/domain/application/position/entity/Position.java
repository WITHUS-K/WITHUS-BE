package KUSITMS.WITHUS.domain.application.position.entity;

import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "POSITION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Position extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POSITION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUITMENT_ID", nullable = false)
    private Recruitment recruitment;

    @Column(nullable = false)
    private String name;

    public void associateRecruitment(Recruitment recruitment) {
        this.recruitment = recruitment;
    }
}
