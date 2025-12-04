package KUSITMS.WITHUS.domain.recruitment.position.entity;

import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    // Note: Position 엔티티는 더 이상 Application, TimeSlot과 직접 관계가 없습니다.
    // Application과 TimeSlot은 이제 OrganizationRole을 사용합니다.
    // 이 매핑들은 데이터베이스 마이그레이션 후 Position 엔티티가 제거될 때 함께 제거됩니다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUITMENT_ID", nullable = false)
    private Recruitment recruitment;

    public void associateRecruitment(Recruitment recruitment) {
        this.recruitment = recruitment;
    }
}
