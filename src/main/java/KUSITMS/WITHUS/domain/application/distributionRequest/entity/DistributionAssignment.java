package KUSITMS.WITHUS.domain.application.distributionRequest.entity;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DISTRIBUTION_ASSIGNMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DistributionAssignment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISTRIBUTION_REQUEST_ID", nullable = false)
    private DistributionRequest request;

    @Column(name = "POSITION_ID", nullable = false)
    private Long positionId;

    @Column(name = "ORGANIZATION_ROLE_ID", nullable = false)
    private Long organizationRoleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "EVALUATION_TYPE", nullable = false)
    private EvaluationType evaluationType;

    @Column(nullable = false)
    private int count;

    public void updateRequest(DistributionRequest request) {
        this.request = request;
    }
}
